package com.translateai.dto.business.admin.contractMng;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerSaveDTO {

    /** ID khách hàng (null khi tạo mới) */
    private String id;

    /** Họ và tên */
    private String fullName;

    /** Số điện thoại */
    private String phoneNumber;

    /** Địa chỉ email */
    private String email;

    /** Số CCCD/CMND */
    private String citizenId;

    /** Địa chỉ thường trú */
    private String address;

    /** Số giấy phép lái xe */
    private String driverLicense;
}

