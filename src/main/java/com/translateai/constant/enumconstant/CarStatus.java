package com.translateai.constant.enumconstant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CarStatus {
    AVAILABLE("Sẵn sàng"),
    RENTED("Đang cho thuê"),
    MAINTENANCE("Đang bảo dưỡng"),
    IN_TRANSIT("Đang vận chuyển");

    private final String description;
}

