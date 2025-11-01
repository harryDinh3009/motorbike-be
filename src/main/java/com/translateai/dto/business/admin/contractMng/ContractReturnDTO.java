package com.translateai.dto.business.admin.contractMng;

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
 * DTO để cập nhật thông tin nhận xe
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContractReturnDTO {
    @NotBlank(message = "Contract ID không được để trống")
    private String contractId;
    
    // Danh sách xe với odometer kết thúc
    @NotNull(message = "Danh sách xe không được để trống")
    private List<ContractCarSaveDTO> cars;
    
    // Danh sách phụ thu (nếu có thêm mới)
    private List<SurchargeSaveDTO> surcharges;
    
    // Thông tin nhận xe
    @NotBlank(message = "Nhân viên nhận xe không được để trống")
    private String returnEmployeeId;
    
    @NotNull(message = "Thời gian nhận xe không được để trống")
    private Date returnTime;
    
    private String returnAddress;
    
    // Cập nhật thông tin thuê xe (nếu cần)
    private Boolean updateRentalInfo;
    private Date newStartDate;
    private Date newEndDate;
    private BigDecimal newTotalAmount;
}

