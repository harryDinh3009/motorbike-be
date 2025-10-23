package com.translateai.entity.domain;

import com.translateai.constant.classconstant.EntityProperties;
import com.translateai.entity.base.PrimaryEntity;
import com.translateai.entity.system.RoleEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Nationalized;

import java.util.Date;
import java.util.List;

/**
 * @author HoangDV
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicUpdate
@FieldDefaults (level = AccessLevel.PRIVATE)
@Entity
@Table (name = "user")
@ToString
public class UserEntity extends PrimaryEntity {

    /** Email người dùng */
    @Column (length = EntityProperties.LENGTH_EMAIL)
    String email;

    /** Mật khẩu đã mã hóa */
    @Column (length = EntityProperties.LENGTH_PASSWORD_ENCODER)
    String password;

    /** Họ và tên đầy đủ */
    @Column (length = EntityProperties.LENGTH_NAME)
    @Nationalized
    String fullName;

    /** Tên đăng nhập */
    @Column (length = EntityProperties.LENGTH_CODE)
    String userName;

    /** Giới tính */
    @Column (length = EntityProperties.LENGTH_CODE)
    String gender;

    /** Ngày sinh */
    @Column
    Date dateOfBirth;

    /** Số điện thoại */
    @Column (length = EntityProperties.LENGTH_PHONE)
    String phoneNumber;

    /** URL ảnh đại diện */
    @Column (length = EntityProperties.LENGTH_URL)
    String avatar;

    /** Mô tả về người dùng */
    @Column (length = EntityProperties.LENGTH_DESCRIPTION)
    @Nationalized
    String description;

    /** Mã giới thiệu của người dùng */
    @Column (length = EntityProperties.REFER_CODE)
    String myReferCode;

    /** Trạng thái tài khoản */
    @Column (length = EntityProperties.LENGTH_CODE)
    String status;

    /** Loại đăng nhập (Local, Facebook, Google...) */
    @Column (length = EntityProperties.LENGTH_CODE)
    String loginType;

    /** ID tài khoản Facebook (nếu đăng nhập qua Facebook) */
    @Column (length = EntityProperties.LENGTH_USERNAME)
    String facebookId;

    /** Danh sách vai trò của người dùng */
    @ManyToMany (fetch = FetchType.EAGER)
    @JoinTable (name = "sys_user_role", joinColumns = @JoinColumn (name = "user_id"), inverseJoinColumns = @JoinColumn (name = "rl_id"))
    private List<RoleEntity> roles;

}
