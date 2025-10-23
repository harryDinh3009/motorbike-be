package com.translateai.dto.business.admin.contractMng;

import com.translateai.dto.common.PageableDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerSearchDTO extends PageableDTO {

    private String fullName;

    private String phoneNumber;

    private String citizenId;
}

