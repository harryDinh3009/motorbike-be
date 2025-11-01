package com.motorbikebe.dto.business.admin.surchargeTypeMng;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SurchargeTypeDTO {
    private String id;
    private String name;
    private BigDecimal price;
    private String description;
    private Integer status;
}

