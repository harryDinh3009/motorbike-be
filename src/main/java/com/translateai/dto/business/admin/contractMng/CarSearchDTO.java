package com.translateai.dto.business.admin.contractMng;

import com.translateai.constant.enumconstant.CarStatus;
import com.translateai.dto.common.PageableDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarSearchDTO extends PageableDTO {

    private String name;

    private String licensePlate;

    private CarStatus status;
}

