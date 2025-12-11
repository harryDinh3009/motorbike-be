package com.motorbikebe.business.admin.contractMng.service;

import com.motorbikebe.common.PageableObject;
import com.motorbikebe.dto.business.admin.contractMng.*;
import com.motorbikebe.entity.domain.ContractEntity;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service quản lý hợp đồng (đã nâng cấp)
 */
public interface ContractMngService {

    /**
     * Tìm kiếm hợp đồng với phân trang
     */
    PageableObject<ContractDTO> searchContracts(ContractSearchDTO searchDTO);

    /**
     * Lấy chi tiết hợp đồng
     */
    ContractDTO getContractDetail(String id);

    /**
     * Tạo mới hoặc cập nhật hợp đồng
     */
    ContractEntity saveContract(@Valid ContractSaveDTO saveDTO);

    /**
     * Hủy hợp đồng (chuyển status thành CANCELLED)
     */
    Boolean cancelContract(String id);

    // ========== Contract Cars ==========
    
    /**
     * Lấy danh sách xe trong hợp đồng
     */
    List<ContractCarDTO> getContractCars(String contractId);

    /**
     * Thêm xe mới vào hợp đồng hiện hữu
     */
    ContractCarDTO addContractCar(@Valid ContractCarCreateDTO createDTO);

    /**
     * Cập nhật thông tin xe trong hợp đồng
     */
    ContractCarDTO updateContractCar(String contractCarId, @Valid ContractCarUpdateDTO updateDTO);

    /**
     * Xóa xe khỏi hợp đồng
     */
    Boolean deleteContractCar(String contractCarId);

    // ========== Surcharges ==========
    
    /**
     * Thêm phụ thu cho hợp đồng
     */
    Boolean addSurcharge(@Valid SurchargeSaveDTO saveDTO);

    /**
     * Cập nhật phụ thu hợp đồng
     */
    Boolean updateSurcharge(String id, @Valid SurchargeSaveDTO saveDTO);

    /**
     * Xóa phụ thu
     */
    Boolean deleteSurcharge(String id);

    /**
     * Lấy danh sách phụ thu theo hợp đồng
     */
    List<SurchargeDTO> getSurchargesByContractId(String contractId);

    // ========== Payments ==========
    
    /**
     * Thêm thanh toán cho hợp đồng
     */
    Boolean addPayment(@Valid PaymentTransactionSaveDTO saveDTO);

    /**
     * Xóa thanh toán
     */
    Boolean deletePayment(String id);

    /**
     * Lấy lịch sử thanh toán
     */
    List<PaymentTransactionDTO> getPaymentHistory(String contractId);

    // ========== Delivery & Return ==========
    
    /**
     * Cập nhật thông tin giao xe
     */
    Boolean updateDelivery(@Valid ContractDeliveryDTO deliveryDTO);

    /**
     * Upload ảnh giao xe
     */
    List<String> uploadDeliveryImages(String contractId, List<MultipartFile> files);

    /**
     * Cập nhật thông tin nhận xe
     */
    Boolean updateReturn(@Valid ContractReturnDTO returnDTO);

    /**
     * Kiểm tra quyền trả xe cho hợp đồng
     * @param contractId ID hợp đồng
     * @return true nếu có quyền, throw exception nếu không có quyền
     */
    Boolean checkReturnPermission(String contractId);

    /**
     * Kiểm tra quyền giao xe cho hợp đồng
     * Kiểm tra xem các xe trong hợp đồng có đang trong hợp đồng gần nhất chưa trả hay không
     * @param contractId ID hợp đồng
     * @return true nếu có thể giao xe, throw exception nếu không thể giao xe
     */
    Boolean checkDeliveryPermission(String contractId);

    /**
     * Upload ảnh nhận xe
     */
    List<String> uploadReturnImages(String contractId, List<MultipartFile> files);

    // ========== Complete Contract ==========
    
    /**
     * Đóng hợp đồng (hoàn thành thanh toán)
     */
    Boolean completeContract(@Valid ContractCompleteDTO completeDTO);

    // ========== PDF Generation ==========
    
    /**
     * Tải xuống file PDF hợp đồng
     */
    byte[] downloadContractPDF(String id);

    // ========== Contract Schedule ==========

    /**
     * Lấy dữ liệu lịch đặt xe
     * Trả về danh sách contract_car trong khoảng thời gian với filter theo branch và status
     */
    List<ContractScheduleItemDTO> getContractSchedule(ContractScheduleRequestDTO requestDTO);

    // ========== Car Availability Check ==========

    /**
     * Check availability của nhiều xe cùng lúc
     * Trả về Map<carId, isAvailable>
     */
    java.util.Map<String, Boolean> checkCarsAvailability(CheckCarsAvailabilityDTO requestDTO);

    // ========== Delivery & Pickup Management ==========

    /**
     * Tìm kiếm hợp đồng chờ giao xe (tối ưu)
     */
    PageableObject<ContractDTO> searchDeliveryContracts(DeliveryPickupSearchDTO searchDTO);

    /**
     * Tìm kiếm hợp đồng chờ nhận xe (tối ưu)
     */
    PageableObject<ContractDTO> searchPickupContracts(DeliveryPickupSearchDTO searchDTO);
}
