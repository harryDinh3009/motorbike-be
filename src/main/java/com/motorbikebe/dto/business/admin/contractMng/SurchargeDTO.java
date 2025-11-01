package com.motorbikebe.dto.business.admin.contractMng;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

