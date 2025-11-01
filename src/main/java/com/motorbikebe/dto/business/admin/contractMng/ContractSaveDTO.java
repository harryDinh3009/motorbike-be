package com.motorbikebe.dto.business.admin.contractMng;

import com.motorbikebe.constant.enumconstant.ContractStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * DTO để tạo/cập nhật hợp đồng
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContractSaveDTO {
    private String id;
    
    @NotBlank(message = "Khách hàng không được để trống")
    private String customerId;
    
    private String source; // Walk-in, Facebook, Hotline, Zalo
    
    @NotNull(message = "Ngày thuê không được để trống")
    private Date startDate;
    
    @NotNull(message = "Ngày trả không được để trống")
    private Date endDate;
    
    private String pickupBranchId;
    private String returnBranchId;
    private String pickupAddress;
    private String returnAddress;
    private Boolean needPickupDelivery;
    private Boolean needReturnDelivery;
    private String notes;
    
    // Danh sách xe thuê
    @NotNull(message = "Phải có ít nhất 1 xe")
    private List<ContractCarSaveDTO> cars;
    
    // Danh sách phụ thu
    private List<SurchargeSaveDTO> surcharges;
    
    // Thông tin thanh toán
    private String discountType; // PERCENTAGE / AMOUNT
    private BigDecimal discountValue;
    private BigDecimal depositAmount;
    
    // Trạng thái
    private ContractStatus status;
}
