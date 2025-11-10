package com.motorbikebe.dto.business.admin.customerMng;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {

    /** ID khách hàng */
    private String id;

    /** Họ và tên */
    private String fullName;

    /** Số điện thoại */
    private String phoneNumber;

    /** Địa chỉ email */
    private String email;

    /** Ngày sinh */
    private Date dateOfBirth;

    /** Giới tính */
    private String gender;

    /** Quốc gia */
    private String country;

    /** Địa chỉ thường trú */
    private String address;

    /** Số CCCD/CMND */
    private String citizenId;

    /** URL ảnh CCCD/CMND mặt trước */
    private String citizenIdFrontImageUrl;

    /** URL ảnh CCCD/CMND mặt sau */
    private String citizenIdBackImageUrl;

    /** Số giấy phép lái xe */
    private String driverLicense;

    /** URL ảnh bằng lái xe */
    private String driverLicenseImageUrl;

    /** Số hộ chiếu */
    private String passport;

    /** URL ảnh hộ chiếu */
    private String passportImageUrl;

    /** Ghi chú */
    private String note;

    /** Tổng tiền đã chi tiêu */
    private BigDecimal totalSpent;
}

