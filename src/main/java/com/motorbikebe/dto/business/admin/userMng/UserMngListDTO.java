package com.motorbikebe.dto.business.admin.userMng;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserMngListDTO {

    private Integer rowNum;

    private String id;

    private String userName;

    private String fullName;

    private String email;

    private String genderNm;

    private String roleNm;

    private String roleCd;

    private String phoneNumber;
    
    private String address;
    
    private String branchId;
    
    private String branchName;

    private String statusNm;

    private String avatar;

}
