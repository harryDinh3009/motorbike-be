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
 * DTO để đóng hợp đồng (hoàn thành thanh toán)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContractCompleteDTO {
    @NotBlank(message = "Contract ID không được để trống")
    private String contractId;
    
    @NotNull(message = "Ngày đóng hợp đồng không được để trống")
    private Date completedDate;
    
    // Thanh toán cuối cùng (nếu có)
    private BigDecimal finalPaymentAmount;
    private String paymentMethod;
    private String paymentNotes;
}

