package com.translateai.dto.business.admin.contractMng;

import com.translateai.constant.enumconstant.ContractStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ContractSaveDTO {

    private String id;

    private String carId;

    private String customerId;

    private Long startDate;

    private Long endDate;

    private Integer rentalDays;

    private BigDecimal dailyPrice;

    private BigDecimal totalAmount;

    private BigDecimal surchargeAmount;

    private BigDecimal finalAmount;

    private ContractStatus status;

    private String notes;

    private Long actualEndDate;
}

