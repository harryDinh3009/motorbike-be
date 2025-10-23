package com.translateai.entity.domain;

import com.translateai.constant.enumconstant.CarStatus;
import com.translateai.entity.base.PrimaryEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entity quản lý thông tin xe
 */
@Entity
@Table(name = "car")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarEntity extends PrimaryEntity {

    /** Tên xe */
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    /** Biển số xe */
    @Column(name = "license_plate", nullable = false, unique = true, length = 20)
    private String licensePlate;

    /** Loại xe (VD: Sedan, SUV, Hatchback...) */
    @Column(name = "car_type", length = 100)
    private String carType;

    /** Giá thuê theo ngày */
    @Column(name = "daily_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal dailyPrice;

    /** Trạng thái xe (Có sẵn, Đang thuê, Đang bảo trì, Đang di chuyển) */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CarStatus status = CarStatus.AVAILABLE;

    /** URL hình ảnh xe */
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    /** Mô tả chi tiết về xe */
    @Lob
    @Column(name = "description")
    private String description;
}

