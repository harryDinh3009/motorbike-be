package com.translateai.constant.enumconstant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContractStatus {
    NEW("Mới tạo"),
    RENTING("Đang thuê"),
    COMPLETED("Đã kết thúc"),
    CANCELLED("Đã hủy");

    private final String description;
}

