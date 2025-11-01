package com.motorbikebe.dto.business.admin.contractMng;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * DTO hiển thị lịch sử thanh toán
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransactionDTO {
    private String id;
    private String transactionCode; // Mã TT
    private String contractId;
    private String paymentMethod; // Phương thức
    private BigDecimal amount; // Số tiền
    private Date paymentDate; // Ngày thanh toán
    private String employeeId;
    private String employeeName; // Nhân viên (join)
    private String notes; // Ghi chú
    private String status;
}

