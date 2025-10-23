package com.translateai.dto.business.admin.contractMng;

import com.translateai.constant.enumconstant.CarStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CarSaveDTO {

    private String id;

    private String name;

    private String licensePlate;

    private String carType;

    private BigDecimal dailyPrice;

    private CarStatus status;

    private String imageUrl;

    private String description;
}

