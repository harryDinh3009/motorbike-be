package com.translateai.entity.domain;

import com.translateai.entity.base.PrimaryEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity quản lý thông tin khách hàng
 */
@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEntity extends PrimaryEntity {

    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "citizen_id", length = 20)
    private String citizenId;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "driver_license", length = 50)
    private String driverLicense;
}

