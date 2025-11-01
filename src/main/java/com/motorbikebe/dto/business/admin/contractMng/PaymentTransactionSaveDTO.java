package com.motorbikebe.dto.business.admin.contractMng;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * DTO để lưu thanh toán
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransactionSaveDTO {
    private String id;
    
    @NotBlank(message = "Contract ID không được để trống")
    private String contractId;
    
    @NotBlank(message = "Phương thức thanh toán không được để trống")
    private String paymentMethod;
    
    @NotNull(message = "Số tiền không được để trống")
    private BigDecimal amount;
    
    @NotNull(message = "Ngày thanh toán không được để trống")
    private Date paymentDate;
    
    private String employeeId;
    private String notes;
}

