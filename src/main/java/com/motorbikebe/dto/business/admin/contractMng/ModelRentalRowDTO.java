package com.motorbikebe.dto.business.admin.contractMng;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class ModelRentalRowDTO {
    private int stt;
    private String modelName;
    private int rentalCount;
    private BigDecimal rentalAmount;
}

