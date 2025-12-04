package com.motorbikebe.dto.business.admin.contractMng;

import com.motorbikebe.constant.enumconstant.CarStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO để lưu xe trong hợp đồng
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContractCarSaveDTO {
    private String id;
    private String carId;
    private BigDecimal dailyPrice;
    private BigDecimal hourlyPrice;
    private BigDecimal totalAmount;
    private Integer startOdometer;
    private Integer endOdometer;
    private String notes;
    private CarStatus status; // Trạng thái xe khi trả (dùng để update vào car entity)
    private String returnStatus; // Trạng thái xe khi trả (lưu vào contract_car)
}

