package com.motorbikebe.entity.domain;

import com.motorbikebe.entity.base.PrimaryEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;

/**
 * Entity quản lý danh mục phụ thu (Surcharge Catalog/Template)
 */
@Entity
@Table(name = "surcharge_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicUpdate
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SurchargeTypeEntity extends PrimaryEntity {

    /** Tên phụ thu (VD: Phí quá giờ, Phí giao nhận xe...) */
    @Column(name = "name", nullable = false, length = 255)
    String name;

    /** Đơn giá mặc định */
    @Column(name = "price", nullable = false, precision = 15, scale = 2)
    BigDecimal price;

    /** Mô tả chi tiết */
    @Lob
    @Column(name = "description")
    String description;

    /** Trạng thái: 1-Active, 0-Inactive */
    @Column(name = "status")
    Integer status;
}

