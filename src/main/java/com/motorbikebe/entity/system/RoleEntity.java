package com.motorbikebe.entity.system;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

@DynamicUpdate
@Entity
@Table(name="sys_role")
@Data
public class RoleEntity {

    /** ID vai trò */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rlId;

    /** Tên vai trò */
    private String rlNm;

    /** Danh mục vai trò */
    private String category;

    /** Thông tin bổ sung */
    private String etc;

    /** Mã vai trò */
    private String rlCd;

    /** Mô tả vai trò */
    private String rlDesc;

}
