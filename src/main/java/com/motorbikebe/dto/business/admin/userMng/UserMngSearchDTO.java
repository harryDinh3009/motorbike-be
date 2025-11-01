package com.motorbikebe.dto.business.admin.userMng;

import com.motorbikebe.dto.common.PageableDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserMngSearchDTO extends PageableDTO {

    private String fullName;

    private String email;

    private String role;

    private String gender;

    private String phoneNumber;

    private String status;

}
