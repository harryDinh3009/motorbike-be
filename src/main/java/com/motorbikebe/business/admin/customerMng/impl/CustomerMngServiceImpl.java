package com.motorbikebe.business.admin.customerMng.impl;

import com.motorbikebe.business.admin.customerMng.service.CustomerMngService;
import com.motorbikebe.common.ApiStatus;
import com.motorbikebe.common.PageableObject;
import com.motorbikebe.config.cloudinary.CloudinaryUploadImages;
import com.motorbikebe.config.exception.RestApiException;
import com.motorbikebe.dto.business.admin.customerMng.CustomerDTO;
import com.motorbikebe.dto.business.admin.customerMng.CustomerSaveDTO;
import com.motorbikebe.dto.business.admin.customerMng.CustomerSearchDTO;
import com.motorbikebe.entity.domain.CustomerEntity;
import com.motorbikebe.repository.business.admin.ContractRepository;
import com.motorbikebe.repository.business.admin.CustomerRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class CustomerMngServiceImpl implements CustomerMngService {

    private final CustomerRepository customerRepository;
    private final ContractRepository contractRepository;
    private final CloudinaryUploadImages cloudinaryUploadImages;
    private final ModelMapper modelMapper;

    @Override
    public PageableObject<CustomerDTO> searchCustomers(CustomerSearchDTO searchDTO) {
        Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, searchDTO.getSize());
        Page<CustomerDTO> customerPage = customerRepository.searchCustomers(pageable, searchDTO);
        return new PageableObject<>(customerPage);
    }

    @Override
    public CustomerDTO getCustomerDetail(String id) {
        Optional<CustomerEntity> customerEntity = customerRepository.findById(id);
        if (!customerEntity.isPresent()) {
            throw new RestApiException(ApiStatus.NOT_FOUND);
        }

        return modelMapper.map(customerEntity.get(), CustomerDTO.class);
    }

    @Override
    @Transactional
    public Boolean saveCustomer(@Valid CustomerSaveDTO saveDTO) {
        CustomerEntity customerEntity;
        boolean isNew = StringUtils.isBlank(saveDTO.getId());

        if (isNew) {
            customerEntity = new CustomerEntity();
            customerEntity.setTotalSpent(BigDecimal.ZERO);
        } else {
            Optional<CustomerEntity> customerEntityFind = customerRepository.findById(saveDTO.getId());
            if (!customerEntityFind.isPresent()) {
                throw new RestApiException(ApiStatus.NOT_FOUND);
            }
            customerEntity = customerEntityFind.get();
        }

        customerEntity.setFullName(saveDTO.getFullName());
        customerEntity.setPhoneNumber(saveDTO.getPhoneNumber());
        customerEntity.setEmail(saveDTO.getEmail());
        customerEntity.setDateOfBirth(saveDTO.getDateOfBirth());
        customerEntity.setGender(saveDTO.getGender());
        customerEntity.setCountry(saveDTO.getCountry());
        customerEntity.setAddress(saveDTO.getAddress());
        customerEntity.setCitizenId(saveDTO.getCitizenId());
        customerEntity.setCitizenIdFrontImageUrl(saveDTO.getCitizenIdFrontImageUrl());
        customerEntity.setCitizenIdBackImageUrl(saveDTO.getCitizenIdBackImageUrl());
        customerEntity.setDriverLicense(saveDTO.getDriverLicense());
        customerEntity.setDriverLicenseImageUrl(saveDTO.getDriverLicenseImageUrl());
        customerEntity.setPassport(saveDTO.getPassport());
        customerEntity.setPassportImageUrl(saveDTO.getPassportImageUrl());
        customerEntity.setNote(saveDTO.getNote());

        customerRepository.save(customerEntity);
        return true;
    }

    @Override
    @Transactional
    public Boolean deleteCustomer(String id) {
        Optional<CustomerEntity> customerEntity = customerRepository.findById(id);
        if (!customerEntity.isPresent()) {
            throw new RestApiException(ApiStatus.NOT_FOUND);
        }

        // Kiểm tra xem khách hàng có hợp đồng nào không
        List<com.motorbikebe.entity.domain.ContractEntity> contracts = contractRepository.findByCustomerId(id);
        if (contracts != null && !contracts.isEmpty()) {
            throw new RestApiException(ApiStatus.CANNOT_DELETE_CUSTOMER_HAS_CONTRACTS);
        }

        customerRepository.deleteById(id);
        return true;
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        List<CustomerEntity> customers = customerRepository.findAll();
        return customers.stream()
                .map(customer -> modelMapper.map(customer, CustomerDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String uploadCitizenIdFrontImage(String customerId, MultipartFile file) {
        Optional<CustomerEntity> customerEntity = customerRepository.findById(customerId);
        if (!customerEntity.isPresent()) {
            throw new RestApiException(ApiStatus.NOT_FOUND);
        }

        String imageUrl = cloudinaryUploadImages.uploadImage(file, "customer/citizen_id_front");

        CustomerEntity customer = customerEntity.get();
        customer.setCitizenIdFrontImageUrl(imageUrl);
        customerRepository.save(customer);

        return imageUrl;
    }

    @Override
    @Transactional
    public String uploadCitizenIdBackImage(String customerId, MultipartFile file) {
        Optional<CustomerEntity> customerEntity = customerRepository.findById(customerId);
        if (!customerEntity.isPresent()) {
            throw new RestApiException(ApiStatus.NOT_FOUND);
        }

        String imageUrl = cloudinaryUploadImages.uploadImage(file, "customer/citizen_id_back");

        CustomerEntity customer = customerEntity.get();
        customer.setCitizenIdBackImageUrl(imageUrl);
        customerRepository.save(customer);

        return imageUrl;
    }

    @Override
    @Transactional
    public String uploadDriverLicenseImage(String customerId, MultipartFile file) {
        Optional<CustomerEntity> customerEntity = customerRepository.findById(customerId);
        if (!customerEntity.isPresent()) {
            throw new RestApiException(ApiStatus.NOT_FOUND);
        }

        String imageUrl = cloudinaryUploadImages.uploadImage(file, "customer/driver_license");

        CustomerEntity customer = customerEntity.get();
        customer.setDriverLicenseImageUrl(imageUrl);
        customerRepository.save(customer);

        return imageUrl;
    }

    @Override
    @Transactional
    public String uploadPassportImage(String customerId, MultipartFile file) {
        Optional<CustomerEntity> customerEntity = customerRepository.findById(customerId);
        if (!customerEntity.isPresent()) {
            throw new RestApiException(ApiStatus.NOT_FOUND);
        }

        String imageUrl = cloudinaryUploadImages.uploadImage(file, "customer/passport");

        CustomerEntity customer = customerEntity.get();
        customer.setPassportImageUrl(imageUrl);
        customerRepository.save(customer);

        return imageUrl;
    }
}

