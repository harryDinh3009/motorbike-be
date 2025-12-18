package com.motorbikebe.dto.business.admin.carMng;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO tạo/cập nhật mẫu xe
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarModelSaveDTO {

    @NotBlank(message = "Tên mẫu xe không được để trống")
    private String name;

    private String brand; // Legacy field

    private String brandId;

    private String description;

    private BigDecimal baseDailyPrice;

    private BigDecimal baseHourlyPrice;

    private Boolean active;
}

