package com.translateai.dto.business.admin.contractMng;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SurchargeDTO {

    /** ID phụ phí */
    private String id;

    /** ID hợp đồng */
    private String contractId;

    /** Mô tả phụ phí */
    private String description;

    /** Số tiền phụ phí */
    private BigDecimal amount;

    /** Ghi chú */
    private String notes;
}

