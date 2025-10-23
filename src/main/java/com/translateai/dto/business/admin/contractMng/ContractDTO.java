package com.translateai.dto.business.admin.contractMng;

import com.translateai.constant.enumconstant.ContractStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ContractDTO {

    private Integer rowNum;

    private String id;

    private String carId;

    private String carName;

    private String licensePlate;

    private String customerId;

    private String customerName;

    private String phoneNumber;

    private Long startDate;

    private Long endDate;

    private Integer rentalDays;

    private BigDecimal dailyPrice;

    private BigDecimal totalAmount;

    private BigDecimal surchargeAmount;

    private BigDecimal finalAmount;

    private ContractStatus status;

    private String statusNm;

    private String contractFileUrl;

    private String notes;

    private Long actualEndDate;

    private List<SurchargeDTO> surcharges;
}

