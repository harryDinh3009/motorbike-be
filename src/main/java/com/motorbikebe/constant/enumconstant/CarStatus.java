package com.motorbikebe.constant.enumconstant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CarStatus {
    AVAILABLE("Hoạt động"),
    NOT_AVAILABLE("Không sẵn sàng"),
    LOST("Bị mất"),
    MAINTENANCE("Đang bảo dưỡng");

    private final String description;
}

