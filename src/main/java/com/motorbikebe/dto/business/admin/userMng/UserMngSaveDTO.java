package com.motorbikebe.dto.business.admin.userMng;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserMngSaveDTO {

    private String id;

    private String username;

    private String fullName;

    private String email;
    
    private String password; // For creating new user

    private String roleCd;

    private String genderCd;

    private String phoneNumber;

    private String dateOfBirth;
    
    private String address;
    
    private String branchId;

    private String statusCd;

}
