package com.motorbikebe.business.admin.contractMng.impl;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.motorbikebe.business.admin.contractMng.service.ContractMngService;
import com.motorbikebe.common.ApiStatus;
import com.motorbikebe.common.PageableObject;
import com.motorbikebe.config.cloudinary.CloudinaryUploadImages;
import com.motorbikebe.config.exception.RestApiException;
import com.motorbikebe.constant.enumconstant.ContractStatus;
import com.motorbikebe.dto.business.admin.contractMng.*;
import com.motorbikebe.entity.domain.*;
import com.motorbikebe.repository.business.admin.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Implementation quản lý hợp đồng (đã nâng cấp hoàn toàn)
 */
@Service
@RequiredArgsConstructor
@Validated
@Slf4j
public class ContractMngServiceImpl implements ContractMngService {

    private final ContractRepository contractRepository;
    private final ContractCarRepository contractCarRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final ContractImageRepository contractImageRepository;
    private final CarRepository carRepository;
    private final CustomerRepository customerRepository;
    private final BranchRepository branchRepository;
    private final EmployeeRepository employeeRepository;
    private final SurchargeRepository surchargeRepository;
    private final CloudinaryUploadImages cloudinaryUploadImages;
    private final ModelMapper modelMapper;

    @Override
    public PageableObject<ContractDTO> searchContracts(ContractSearchDTO searchDTO) {
        Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, searchDTO.getSize());
        Page<ContractDTO> contractPage = contractRepository.searchContracts(pageable, searchDTO);

        // Set status description
        contractPage.forEach(contract -> {
            if (contract.getStatus() != null) {
                contract.setStatusNm(contract.getStatus().getDescription());
            }
        });

        return PageableObject.<ContractDTO>builder()
                .data(contractPage.getContent())
                .totalRecords(contractPage.getTotalElements())
                .currentPage(searchDTO.getPage())
                .totalPages(contractPage.getTotalPages())
                .build();
    }

    @Override
    public ContractDTO getContractDetail(String id) {
        Optional<ContractEntity> contractOpt = contractRepository.findById(id);
        if (!contractOpt.isPresent()) {
            throw new RestApiException(ApiStatus.NOT_FOUND);
        }

        ContractEntity contract = contractOpt.get();
        ContractDTO contractDTO = modelMapper.map(contract, ContractDTO.class);

        // Set status name
        if (contractDTO.getStatus() != null) {
            contractDTO.setStatusNm(contractDTO.getStatus().getDescription());
        }

        // Load customer info
        Optional<CustomerEntity> customer = customerRepository.findById(contract.getCustomerId());
        if (customer.isPresent()) {
            contractDTO.setCustomerName(customer.get().getFullName());
            contractDTO.setPhoneNumber(customer.get().getPhoneNumber());
            contractDTO.setEmail(customer.get().getEmail());
            contractDTO.setCountry(customer.get().getCountry());
            contractDTO.setCitizenId(customer.get().getCitizenId());
            
            // Count total contracts
            int totalContracts = contractRepository.findByCustomerId(contract.getCustomerId()).size();
            contractDTO.setTotalContracts(totalContracts);
        }

        // Load branch info
        if (StringUtils.isNotBlank(contract.getPickupBranchId())) {
            branchRepository.findById(contract.getPickupBranchId())
                    .ifPresent(b -> contractDTO.setPickupBranchName(b.getName()));
        }
        if (StringUtils.isNotBlank(contract.getReturnBranchId())) {
            branchRepository.findById(contract.getReturnBranchId())
                    .ifPresent(b -> contractDTO.setReturnBranchName(b.getName()));
        }

        // Load employee info
        if (StringUtils.isNotBlank(contract.getDeliveryEmployeeId())) {
            employeeRepository.findById(contract.getDeliveryEmployeeId())
                    .ifPresent(e -> contractDTO.setDeliveryEmployeeName(e.getFullName()));
        }
        if (StringUtils.isNotBlank(contract.getReturnEmployeeId())) {
            employeeRepository.findById(contract.getReturnEmployeeId())
                    .ifPresent(e -> contractDTO.setReturnEmployeeName(e.getFullName()));
        }

        // Load cars
        contractDTO.setCars(getContractCars(id));

        // Load surcharges
        contractDTO.setSurcharges(getSurchargesByContractId(id));

        // Load payments
        contractDTO.setPayments(getPaymentHistory(id));

        // Load images
        List<ContractImageEntity> deliveryImages = contractImageRepository.findByContractIdAndImageType(id, "DELIVERY");
        contractDTO.setDeliveryImages(deliveryImages.stream()
                .map(img -> modelMapper.map(img, ContractImageDTO.class))
                .collect(Collectors.toList()));

        List<ContractImageEntity> returnImages = contractImageRepository.findByContractIdAndImageType(id, "RETURN");
        contractDTO.setReturnImages(returnImages.stream()
                .map(img -> modelMapper.map(img, ContractImageDTO.class))
                .collect(Collectors.toList()));

        return contractDTO;
    }

    @Override
    @Transactional
    public Boolean saveContract(@Valid ContractSaveDTO saveDTO) {
        ContractEntity contractEntity;
        boolean isNew = StringUtils.isBlank(saveDTO.getId());

        if (isNew) {
            contractEntity = new ContractEntity();
            
            // Generate contract code
            String contractCode = contractRepository.generateContractCode();
            contractEntity.setContractCode(contractCode != null ? contractCode : "HD000001");
            
            contractEntity.setStatus(saveDTO.getStatus() != null ? saveDTO.getStatus() : ContractStatus.DRAFT);
        } else {
            Optional<ContractEntity> existingContract = contractRepository.findById(saveDTO.getId());
            if (!existingContract.isPresent()) {
                throw new RestApiException(ApiStatus.NOT_FOUND);
            }
            contractEntity = existingContract.get();
        }

        // Validate customer exists
        Optional<CustomerEntity> customer = customerRepository.findById(saveDTO.getCustomerId());
        if (!customer.isPresent()) {
            throw new RestApiException(ApiStatus.NOT_FOUND);
        }

        // Validate cars availability (nếu status CONFIRMED trở lên)
        if (saveDTO.getStatus() != null && 
            (saveDTO.getStatus() == ContractStatus.CONFIRMED || saveDTO.getStatus() == ContractStatus.DELIVERED)) {
            for (ContractCarSaveDTO carDTO : saveDTO.getCars()) {
                List<ContractEntity> overlappingContracts = contractRepository.findOverlappingContracts(
                        carDTO.getCarId(), saveDTO.getStartDate(), saveDTO.getEndDate());
                
                // Loại trừ contract hiện tại nếu đang update
                if (!isNew) {
                    overlappingContracts = overlappingContracts.stream()
                            .filter(c -> !c.getId().equals(saveDTO.getId()))
                            .collect(Collectors.toList());
                }
                
                if (!overlappingContracts.isEmpty()) {
                    throw new RestApiException(ApiStatus.BAD_REQUEST);
                }
            }
        }

        // Set basic info
        contractEntity.setCustomerId(saveDTO.getCustomerId());
        contractEntity.setSource(saveDTO.getSource());
        contractEntity.setStartDate(saveDTO.getStartDate());
        contractEntity.setEndDate(saveDTO.getEndDate());
        contractEntity.setPickupBranchId(saveDTO.getPickupBranchId());
        contractEntity.setReturnBranchId(saveDTO.getReturnBranchId());
        contractEntity.setPickupAddress(saveDTO.getPickupAddress());
        contractEntity.setReturnAddress(saveDTO.getReturnAddress());
        contractEntity.setNeedPickupDelivery(saveDTO.getNeedPickupDelivery());
        contractEntity.setNeedReturnDelivery(saveDTO.getNeedReturnDelivery());
        contractEntity.setNotes(saveDTO.getNotes());

        // Calculate financial info
        BigDecimal totalRentalAmount = saveDTO.getCars().stream()
                .map(ContractCarSaveDTO::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSurcharge = saveDTO.getSurcharges() != null ? 
                saveDTO.getSurcharges().stream()
                        .map(SurchargeSaveDTO::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add) : BigDecimal.ZERO;

        BigDecimal discountAmount = calculateDiscountAmount(
                totalRentalAmount, 
                saveDTO.getDiscountType(), 
                saveDTO.getDiscountValue());

        BigDecimal finalAmount = totalRentalAmount.add(totalSurcharge).subtract(discountAmount);

        contractEntity.setTotalRentalAmount(totalRentalAmount);
        contractEntity.setTotalSurcharge(totalSurcharge);
        contractEntity.setDiscountType(saveDTO.getDiscountType());
        contractEntity.setDiscountValue(saveDTO.getDiscountValue());
        contractEntity.setDiscountAmount(discountAmount);
        contractEntity.setDepositAmount(saveDTO.getDepositAmount() != null ? saveDTO.getDepositAmount() : BigDecimal.ZERO);
        contractEntity.setFinalAmount(finalAmount);
        
        // Calculate paid and remaining amount
        BigDecimal paidAmount = paymentTransactionRepository.sumAmountByContractId(contractEntity.getId());
        contractEntity.setPaidAmount(paidAmount);
        contractEntity.setRemainingAmount(finalAmount.subtract(paidAmount));

        // Save contract
        ContractEntity savedContract = contractRepository.save(contractEntity);

        // Save cars
        if (isNew) {
            for (ContractCarSaveDTO carDTO : saveDTO.getCars()) {
                ContractCarEntity contractCar = modelMapper.map(carDTO, ContractCarEntity.class);
                contractCar.setContractId(savedContract.getId());
                contractCarRepository.save(contractCar);
            }
        }

        // Save surcharges
        if (isNew && saveDTO.getSurcharges() != null) {
            for (SurchargeSaveDTO surchargeDTO : saveDTO.getSurcharges()) {
                SurchargeEntity surcharge = modelMapper.map(surchargeDTO, SurchargeEntity.class);
                surcharge.setContractId(savedContract.getId());
                surchargeRepository.save(surcharge);
            }
        }

        return true;
    }

    @Override
    @Transactional
    public Boolean deleteContract(String id) {
        Optional<ContractEntity> contractOpt = contractRepository.findById(id);
        if (!contractOpt.isPresent()) {
            throw new RestApiException(ApiStatus.NOT_FOUND);
        }

        // Delete related data
        contractCarRepository.deleteByContractId(id);
        surchargeRepository.deleteByContractId(id);
        paymentTransactionRepository.deleteByContractId(id);
        contractImageRepository.deleteByContractId(id);

        // Delete contract
        contractRepository.deleteById(id);
        return true;
    }

    // ========== Contract Cars ==========

    @Override
    public List<ContractCarDTO> getContractCars(String contractId) {
        List<ContractCarEntity> contractCars = contractCarRepository.findByContractId(contractId);
        
        return contractCars.stream().map(contractCar -> {
            ContractCarDTO dto = modelMapper.map(contractCar, ContractCarDTO.class);
            
            // Load car info
            carRepository.findById(contractCar.getCarId()).ifPresent(car -> {
                dto.setCarModel(car.getModel());
                dto.setCarType(car.getCarType());
                dto.setLicensePlate(car.getLicensePlate());
            });
            
            return dto;
        }).collect(Collectors.toList());
    }

    // ========== Surcharges ==========

    @Override
    @Transactional
    public Boolean addSurcharge(@Valid SurchargeSaveDTO saveDTO) {
        SurchargeEntity surcharge = modelMapper.map(saveDTO, SurchargeEntity.class);
        surchargeRepository.save(surcharge);
        
        // Update contract total
        updateContractTotals(saveDTO.getContractId());
        return true;
    }

    @Override
    @Transactional
    public Boolean deleteSurcharge(String id) {
        Optional<SurchargeEntity> surchargeOpt = surchargeRepository.findById(id);
        if (!surchargeOpt.isPresent()) {
            throw new RestApiException(ApiStatus.NOT_FOUND);
        }
        
        String contractId = surchargeOpt.get().getContractId();
        surchargeRepository.deleteById(id);
        
        // Update contract total
        updateContractTotals(contractId);
        return true;
    }

    @Override
    public List<SurchargeDTO> getSurchargesByContractId(String contractId) {
        return surchargeRepository.findByContractId(contractId);
    }

    // ========== Payments ==========

    @Override
    @Transactional
    public Boolean addPayment(@Valid PaymentTransactionSaveDTO saveDTO) {
        PaymentTransactionEntity payment = modelMapper.map(saveDTO, PaymentTransactionEntity.class);
        
        // Generate transaction code
        String transactionCode = "TT" + System.currentTimeMillis();
        payment.setTransactionCode(transactionCode);
        payment.setStatus("SUCCESS");
        
        paymentTransactionRepository.save(payment);
        
        // Update contract paid amount
        updateContractTotals(saveDTO.getContractId());
        return true;
    }

    @Override
    @Transactional
    public Boolean deletePayment(String id) {
        Optional<PaymentTransactionEntity> paymentOpt = paymentTransactionRepository.findById(id);
        if (!paymentOpt.isPresent()) {
            throw new RestApiException(ApiStatus.NOT_FOUND);
        }
        
        String contractId = paymentOpt.get().getContractId();
        paymentTransactionRepository.deleteById(id);
        
        // Update contract paid amount
        updateContractTotals(contractId);
        return true;
    }

    @Override
    public List<PaymentTransactionDTO> getPaymentHistory(String contractId) {
        return paymentTransactionRepository.findByContractIdWithEmployee(contractId);
    }

    // ========== Delivery & Return ==========

    @Override
    @Transactional
    public Boolean updateDelivery(@Valid ContractDeliveryDTO deliveryDTO) {
        Optional<ContractEntity> contractOpt = contractRepository.findById(deliveryDTO.getContractId());
        if (!contractOpt.isPresent()) {
            throw new RestApiException(ApiStatus.NOT_FOUND);
        }

        ContractEntity contract = contractOpt.get();
        
        // Update delivery info
        contract.setDeliveryEmployeeId(deliveryDTO.getDeliveryEmployeeId());
        contract.setDeliveryTime(deliveryDTO.getDeliveryTime());
        contract.setStatus(ContractStatus.DELIVERED);
        
        if (StringUtils.isNotBlank(deliveryDTO.getPickupAddress())) {
            contract.setPickupAddress(deliveryDTO.getPickupAddress());
        }
        
        // Update rental info if needed
        if (Boolean.TRUE.equals(deliveryDTO.getUpdateRentalInfo())) {
            if (deliveryDTO.getNewStartDate() != null) {
                contract.setStartDate(deliveryDTO.getNewStartDate());
            }
            if (deliveryDTO.getNewEndDate() != null) {
                contract.setEndDate(deliveryDTO.getNewEndDate());
            }
            if (deliveryDTO.getNewTotalAmount() != null) {
                contract.setTotalRentalAmount(deliveryDTO.getNewTotalAmount());
            }
        }
        
        contractRepository.save(contract);
        
        // Update cars odometer
        for (ContractCarSaveDTO carDTO : deliveryDTO.getCars()) {
            if (carDTO.getId() != null) {
                contractCarRepository.findById(carDTO.getId()).ifPresent(contractCar -> {
                    contractCar.setStartOdometer(carDTO.getStartOdometer());
                    contractCarRepository.save(contractCar);
                });
            }
        }
        
        // Add surcharges if any
        if (deliveryDTO.getSurcharges() != null) {
            for (SurchargeSaveDTO surchargeDTO : deliveryDTO.getSurcharges()) {
                addSurcharge(surchargeDTO);
            }
        }
        
        return true;
    }

    @Override
    @Transactional
    public List<String> uploadDeliveryImages(String contractId, List<MultipartFile> files) {
        List<String> imageUrls = new ArrayList<>();
        
        for (int i = 0; i < files.size(); i++) {
            String imageUrl = cloudinaryUploadImages.uploadImage(files.get(i), "contract-delivery-images");
            
            ContractImageEntity image = new ContractImageEntity();
            image.setContractId(contractId);
            image.setImageType("DELIVERY");
            image.setImageUrl(imageUrl);
            image.setDisplayOrder(i + 1);
            contractImageRepository.save(image);
            
            imageUrls.add(imageUrl);
        }
        
        return imageUrls;
    }

    @Override
    @Transactional
    public Boolean updateReturn(@Valid ContractReturnDTO returnDTO) {
        Optional<ContractEntity> contractOpt = contractRepository.findById(returnDTO.getContractId());
        if (!contractOpt.isPresent()) {
            throw new RestApiException(ApiStatus.NOT_FOUND);
        }

        ContractEntity contract = contractOpt.get();
        
        // Update return info
        contract.setReturnEmployeeId(returnDTO.getReturnEmployeeId());
        contract.setReturnTime(returnDTO.getReturnTime());
        contract.setStatus(ContractStatus.RETURNED);
        
        if (StringUtils.isNotBlank(returnDTO.getReturnAddress())) {
            contract.setReturnAddress(returnDTO.getReturnAddress());
        }
        
        // Update rental info if needed
        if (Boolean.TRUE.equals(returnDTO.getUpdateRentalInfo())) {
            if (returnDTO.getNewStartDate() != null) {
                contract.setStartDate(returnDTO.getNewStartDate());
            }
            if (returnDTO.getNewEndDate() != null) {
                contract.setEndDate(returnDTO.getNewEndDate());
            }
            if (returnDTO.getNewTotalAmount() != null) {
                contract.setTotalRentalAmount(returnDTO.getNewTotalAmount());
            }
        }
        
        contractRepository.save(contract);
        
        // Update cars odometer
        for (ContractCarSaveDTO carDTO : returnDTO.getCars()) {
            if (carDTO.getId() != null) {
                contractCarRepository.findById(carDTO.getId()).ifPresent(contractCar -> {
                    contractCar.setEndOdometer(carDTO.getEndOdometer());
                    contractCarRepository.save(contractCar);
                });
            }
        }
        
        // Add surcharges if any
        if (returnDTO.getSurcharges() != null) {
            for (SurchargeSaveDTO surchargeDTO : returnDTO.getSurcharges()) {
                addSurcharge(surchargeDTO);
            }
        }
        
        return true;
    }

    @Override
    @Transactional
    public List<String> uploadReturnImages(String contractId, List<MultipartFile> files) {
        List<String> imageUrls = new ArrayList<>();
        
        for (int i = 0; i < files.size(); i++) {
            String imageUrl = cloudinaryUploadImages.uploadImage(files.get(i), "contract-return-images");
            
            ContractImageEntity image = new ContractImageEntity();
            image.setContractId(contractId);
            image.setImageType("RETURN");
            image.setImageUrl(imageUrl);
            image.setDisplayOrder(i + 1);
            contractImageRepository.save(image);
            
            imageUrls.add(imageUrl);
        }
        
        return imageUrls;
    }

    // ========== Complete Contract ==========

    @Override
    @Transactional
    public Boolean completeContract(@Valid ContractCompleteDTO completeDTO) {
        Optional<ContractEntity> contractOpt = contractRepository.findById(completeDTO.getContractId());
        if (!contractOpt.isPresent()) {
            throw new RestApiException(ApiStatus.NOT_FOUND);
        }

        ContractEntity contract = contractOpt.get();
        
        // Add final payment if any
        if (completeDTO.getFinalPaymentAmount() != null && 
            completeDTO.getFinalPaymentAmount().compareTo(BigDecimal.ZERO) > 0) {
            
            PaymentTransactionSaveDTO paymentDTO = new PaymentTransactionSaveDTO();
            paymentDTO.setContractId(completeDTO.getContractId());
            paymentDTO.setPaymentMethod(completeDTO.getPaymentMethod());
            paymentDTO.setAmount(completeDTO.getFinalPaymentAmount());
            paymentDTO.setPaymentDate(completeDTO.getCompletedDate());
            paymentDTO.setNotes(completeDTO.getPaymentNotes());
            
            addPayment(paymentDTO);
        }
        
        // Update contract status
        contract.setStatus(ContractStatus.COMPLETED);
        contract.setCompletedDate(completeDTO.getCompletedDate());
        contractRepository.save(contract);
        
        return true;
    }

    // ========== PDF Generation ==========

    @Override
    public byte[] downloadContractPDF(String id) {
        ContractDTO contract = getContractDetail(id);
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            // Add contract content
            document.add(new Paragraph("HỢP ĐỒNG THUÊ XE")
                    .setFontSize(18)
                    .setBold());
            
            document.add(new Paragraph("Mã hợp đồng: " + contract.getContractCode()));
            document.add(new Paragraph("Khách hàng: " + contract.getCustomerName()));
            document.add(new Paragraph("SĐT: " + contract.getPhoneNumber()));
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            document.add(new Paragraph("Ngày thuê: " + sdf.format(contract.getStartDate())));
            document.add(new Paragraph("Ngày trả: " + sdf.format(contract.getEndDate())));
            
            // Add cars table
            document.add(new Paragraph("\nDANH SÁCH XE:").setBold());
            Table table = new Table(4);
            table.addCell("STT");
            table.addCell("Xe");
            table.addCell("Biển số");
            table.addCell("Giá thuê");
            
            int i = 1;
            for (ContractCarDTO car : contract.getCars()) {
                table.addCell(String.valueOf(i++));
                table.addCell(car.getCarModel());
                table.addCell(car.getLicensePlate());
                table.addCell(car.getTotalAmount().toString());
            }
            document.add(table);
            
            // Add totals
            document.add(new Paragraph("\nTổng tiền thuê xe: " + contract.getTotalRentalAmount()));
            document.add(new Paragraph("Tổng phụ thu: " + contract.getTotalSurcharge()));
            document.add(new Paragraph("Giảm giá: " + contract.getDiscountAmount()));
            document.add(new Paragraph("Tổng cộng: " + contract.getFinalAmount()).setBold());
            
            document.close();
            return baos.toByteArray();
            
        } catch (Exception e) {
            log.error("Error generating PDF", e);
            throw new RestApiException(ApiStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ========== Helper Methods ==========

    private BigDecimal calculateDiscountAmount(BigDecimal totalAmount, String discountType, BigDecimal discountValue) {
        if (discountValue == null || discountValue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        if ("PERCENTAGE".equals(discountType)) {
            return totalAmount.multiply(discountValue).divide(BigDecimal.valueOf(100));
        } else if ("AMOUNT".equals(discountType)) {
            return discountValue;
        }
        
        return BigDecimal.ZERO;
    }

    private void updateContractTotals(String contractId) {
        Optional<ContractEntity> contractOpt = contractRepository.findById(contractId);
        if (!contractOpt.isPresent()) {
            return;
        }

        ContractEntity contract = contractOpt.get();
        
        // Recalculate surcharge total
        BigDecimal totalSurcharge = surchargeRepository.findByContractId(contractId).stream()
                .map(s -> s.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Recalculate final amount
        BigDecimal finalAmount = contract.getTotalRentalAmount()
                .add(totalSurcharge)
                .subtract(contract.getDiscountAmount() != null ? contract.getDiscountAmount() : BigDecimal.ZERO);
        
        // Recalculate paid amount
        BigDecimal paidAmount = paymentTransactionRepository.sumAmountByContractId(contractId);
        
        contract.setTotalSurcharge(totalSurcharge);
        contract.setFinalAmount(finalAmount);
        contract.setPaidAmount(paidAmount);
        contract.setRemainingAmount(finalAmount.subtract(paidAmount));
        
        contractRepository.save(contract);
    }
}
