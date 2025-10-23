package com.translateai.dto.business.admin.contractMng;

import com.translateai.constant.enumconstant.ContractStatus;
import com.translateai.dto.common.PageableDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContractSearchDTO extends PageableDTO {

    private String customerId;

    private String carId;

    private ContractStatus status;

    private Long startDateFrom;

    private Long startDateTo;
}

