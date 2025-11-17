package com.motorbikebe.dto.business.admin.contractMng;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO tạo mới xe trong hợp đồng hiện hữu
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContractCarCreateDTO {
    @NotBlank(message = "Hợp đồng không được để trống")
    private String contractId;

    @NotBlank(message = "Xe không được để trống")
    private String carId;

    private BigDecimal dailyPrice;
    private BigDecimal hourlyPrice;

    @NotNull(message = "Tổng tiền xe không được để trống")
    private BigDecimal totalAmount;

    private Integer startOdometer;
    private Integer endOdometer;
    private String notes;
}

