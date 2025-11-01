package com.translateai.entity.domain;

import com.translateai.entity.base.PrimaryEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;

/**
 * Entity quản lý danh sách xe trong hợp đồng (một hợp đồng có nhiều xe)
 */
@Entity
@Table(name = "contract_car")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicUpdate
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractCarEntity extends PrimaryEntity {

    /** ID hợp đồng */
    @Column(name = "contract_id", nullable = false, length = 36)
    String contractId;

    /** ID xe */
    @Column(name = "car_id", nullable = false, length = 36)
    String carId;

    /** Giá thuê theo ngày (có thể khác giá gốc của xe) */
    @Column(name = "daily_price", precision = 15, scale = 2)
    BigDecimal dailyPrice;

    /** Giá thuê theo giờ */
    @Column(name = "hourly_price", precision = 15, scale = 2)
    BigDecimal hourlyPrice;

    /** Tổng tiền thuê xe này */
    @Column(name = "total_amount", precision = 15, scale = 2)
    BigDecimal totalAmount;

    /** Odometer xuất phát (khi giao xe) */
    @Column(name = "start_odometer")
    Integer startOdometer;

    /** Odometer kết thúc (khi nhận xe) */
    @Column(name = "end_odometer")
    Integer endOdometer;

    /** Ghi chú cho xe này */
    @Lob
    @Column(name = "notes")
    String notes;
}

