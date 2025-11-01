package com.translateai.entity.domain;

import com.translateai.entity.base.PrimaryEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.DynamicUpdate;

/**
 * Entity quản lý ảnh chụp tình trạng xe khi giao/nhận
 */
@Entity
@Table(name = "contract_image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicUpdate
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractImageEntity extends PrimaryEntity {

    /** ID hợp đồng */
    @Column(name = "contract_id", nullable = false, length = 36)
    String contractId;

    /** Loại ảnh: DELIVERY (giao xe), RETURN (nhận xe) */
    @Column(name = "image_type", length = 20)
    String imageType;

    /** URL ảnh trên Cloudinary */
    @Column(name = "image_url", length = 500)
    String imageUrl;

    /** Thứ tự hiển thị */
    @Column(name = "display_order")
    Integer displayOrder;

    /** Ghi chú */
    @Column(name = "notes", length = 500)
    String notes;
}

