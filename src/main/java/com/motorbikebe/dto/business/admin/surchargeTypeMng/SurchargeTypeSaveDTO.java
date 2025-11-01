package com.motorbikebe.dto.business.admin.surchargeTypeMng;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SurchargeTypeSaveDTO {
    private String id;

    @NotBlank(message = "Tên phụ thu không được để trống")
    private String name;

    @NotNull(message = "Đơn giá không được để trống")
    private BigDecimal price;

    private String description;
    private Integer status;
}

