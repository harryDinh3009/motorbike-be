package com.motorbikebe.dto.business.admin.contractMng;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO cập nhật thông tin xe trong hợp đồng
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContractCarUpdateDTO {
    private String carId;
    private BigDecimal dailyPrice;
    private BigDecimal hourlyPrice;
    private BigDecimal totalAmount;
    private Integer startOdometer;
    private Integer endOdometer;
    private String notes;
}

