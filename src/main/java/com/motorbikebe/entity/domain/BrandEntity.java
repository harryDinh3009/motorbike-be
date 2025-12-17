package com.motorbikebe.entity.domain;

import com.motorbikebe.constant.classconstant.EntityProperties;
import com.motorbikebe.entity.base.PrimaryEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Nationalized;

/**
 * Entity quản lý thông tin hãng xe
 */
@Entity
@Table(name = "brand")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicUpdate
public class BrandEntity extends PrimaryEntity {

    /** Tên hãng xe */
    @Column(name = "name", nullable = false, length = EntityProperties.LENGTH_NAME, unique = true)
    @Nationalized
    private String name;

    /** Mô tả */
    @Column(name = "description", length = 500)
    @Nationalized
    private String description;
}

