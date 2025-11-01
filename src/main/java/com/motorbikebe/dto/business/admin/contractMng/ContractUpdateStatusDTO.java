package com.motorbikebe.dto.business.admin.contractMng;

import com.motorbikebe.constant.enumconstant.ContractStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContractUpdateStatusDTO {

    private String id;

    private ContractStatus status;

    private Long actualEndDate;

    private String notes;
}

