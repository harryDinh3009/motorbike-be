package com.motorbikebe.dto.business.admin.carMng;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO chứa thông tin model để populate vào car
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarModelInfoDTO {
    private String brandId;
    private String brandName;
    private BigDecimal baseDailyPrice;
    private BigDecimal baseHourlyPrice;
}
