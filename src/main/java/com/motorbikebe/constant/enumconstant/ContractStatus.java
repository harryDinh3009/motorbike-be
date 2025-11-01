package com.motorbikebe.constant.enumconstant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContractStatus {
    DRAFT("Nháp"),
    CONFIRMED("Đã xác nhận"),
    DELIVERED("Đã giao xe"),
    RETURNED("Đã trả xe"),
    COMPLETED("Hoàn thành"),
    CANCELLED("Đã hủy");

    private final String description;
}

