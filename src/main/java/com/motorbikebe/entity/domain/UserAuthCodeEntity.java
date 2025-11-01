package com.motorbikebe.entity.domain;

import com.motorbikebe.constant.classconstant.EntityProperties;
import com.motorbikebe.entity.base.PrimaryEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicUpdate
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "user_auth_code")
public class UserAuthCodeEntity extends PrimaryEntity {

    /** Email người dùng */
    @Column(length = EntityProperties.LENGTH_ID)
    String email;

    /** Mã xác thực (OTP) */
    @Column(length = EntityProperties.LENGTH_CODE)
    String code;

}
