package com.translateai.entity.domain;

import com.translateai.constant.classconstant.EntityProperties;
import com.translateai.entity.base.PrimaryEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Entity quản lý thông tin khách hàng
 */
@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
public class CustomerEntity extends PrimaryEntity {

    /** Họ và tên khách hàng */
    @Column(name = "full_name", nullable = false, length = EntityProperties.LENGTH_NAME)
    @Nationalized
    private String fullName;

    /** Số điện thoại */
    @Column(name = "phone_number", nullable = false, length = EntityProperties.LENGTH_PHONE)
    private String phoneNumber;

    /** Địa chỉ email */
    @Column(name = "email", length = EntityProperties.LENGTH_EMAIL)
    private String email;

    /** Ngày sinh */
    @Column(name = "date_of_birth")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    /** Giới tính */
    @Column(name = "gender", length = EntityProperties.LENGTH_CODE)
    private String gender;

    /** Quốc gia */
    @Column(name = "country", length = EntityProperties.LENGTH_NAME)
    @Nationalized
    private String country;

    /** Địa chỉ thường trú */
    @Column(name = "address", length = 500)
    @Nationalized
    private String address;

    /** Số CCCD/CMND */
    @Column(name = "citizen_id", length = 20)
    private String citizenId;

    /** URL ảnh CCCD/CMND */
    @Column(name = "citizen_id_image_url", length = EntityProperties.LENGTH_URL)
    private String citizenIdImageUrl;

    /** Số giấy phép lái xe */
    @Column(name = "driver_license", length = 50)
    private String driverLicense;

    /** URL ảnh bằng lái xe */
    @Column(name = "driver_license_image_url", length = EntityProperties.LENGTH_URL)
    private String driverLicenseImageUrl;

    /** Số hộ chiếu */
    @Column(name = "passport", length = 50)
    private String passport;

    /** URL ảnh hộ chiếu */
    @Column(name = "passport_image_url", length = EntityProperties.LENGTH_URL)
    private String passportImageUrl;

    /** Ghi chú */
    @Lob
    @Column(name = "note")
    @Nationalized
    private String note;

    /** Tổng tiền đã chi tiêu */
    @Column(name = "total_spent", precision = 15, scale = 2)
    private BigDecimal totalSpent;
}

