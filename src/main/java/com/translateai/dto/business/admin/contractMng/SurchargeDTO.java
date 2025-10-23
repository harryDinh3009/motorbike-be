package com.translateai.dto.business.admin.contractMng;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SurchargeDTO {

    private String id;

    private String contractId;

    private String description;

    private BigDecimal amount;

    private String notes;
}

