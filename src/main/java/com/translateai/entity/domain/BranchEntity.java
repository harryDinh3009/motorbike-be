package com.translateai.entity.domain;

import com.translateai.constant.classconstant.EntityProperties;
import com.translateai.entity.base.PrimaryEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Nationalized;

/**
 * Entity quản lý thông tin chi nhánh
 */
@Entity
@Table(name = "branch")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicUpdate
public class BranchEntity extends PrimaryEntity {

    /** Tên chi nhánh */
    @Column(name = "name", nullable = false, length = EntityProperties.LENGTH_NAME)
    @Nationalized
    private String name;

    /** Số điện thoại chi nhánh */
    @Column(name = "phone_number", length = EntityProperties.LENGTH_PHONE)
    private String phoneNumber;

    /** Địa chỉ chi nhánh */
    @Column(name = "address", length = 500)
    @Nationalized
    private String address;

    /** Ghi chú */
    @Lob
    @Column(name = "note")
    @Nationalized
    private String note;

    /** Trạng thái (1: Hoạt động, 0: Ngừng hoạt động) */
    @Column(name = "status", nullable = false)
    private Integer status = 1;
}

