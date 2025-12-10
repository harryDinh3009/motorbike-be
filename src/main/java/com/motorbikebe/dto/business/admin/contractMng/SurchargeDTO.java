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

    /** ID loại phụ thu (tham chiếu đến surcharge_type) */
    private String surchargeTypeId;

    /** Mô tả phụ phí */
    private String description;

    /** Số lượng */
    private BigDecimal quantity;

    /** Đơn giá */
    private BigDecimal unitPrice;

    /** Số tiền phụ phí */
    private BigDecimal amount;

    /** Ghi chú */
    private String notes;
}

