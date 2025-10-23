package com.translateai.entity.domain;

import com.translateai.constant.enumconstant.ContractStatus;
import com.translateai.entity.base.PrimaryEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entity quản lý hợp đồng cho thuê xe
 */
@Entity
@Table(name = "contract")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContractEntity extends PrimaryEntity {

    /** ID xe được thuê */
    @Column(name = "car_id", nullable = false, length = 36)
    private String carId;

    /** ID khách hàng thuê xe */
    @Column(name = "customer_id", nullable = false, length = 36)
    private String customerId;

    /** Ngày bắt đầu thuê (timestamp) */
    @Column(name = "start_date", nullable = false)
    private Long startDate;

    /** Ngày kết thúc thuê dự kiến (timestamp) */
    @Column(name = "end_date", nullable = false)
    private Long endDate;

    /** Số ngày thuê */
    @Column(name = "rental_days", nullable = false)
    private Integer rentalDays;

    /** Giá thuê theo ngày tại thời điểm ký hợp đồng */
    @Column(name = "daily_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal dailyPrice;

    /** Tổng tiền thuê (chưa bao gồm phụ phí) = số ngày × giá thuê/ngày */
    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    /** Tổng phụ phí (phạt trễ, hư hỏng...) */
    @Column(name = "surcharge_amount", precision = 15, scale = 2)
    private BigDecimal surchargeAmount = BigDecimal.ZERO;

    /** Tổng tiền cuối cùng = tổng tiền thuê + tổng phụ phí */
    @Column(name = "final_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal finalAmount;

    /** Trạng thái hợp đồng (Mới tạo, Đang thuê, Hoàn thành, Đã hủy) */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ContractStatus status = ContractStatus.NEW;

    /** Ghi chú hợp đồng */
    @Lob
    @Column(name = "notes")
    private String notes;

    /** Ngày trả xe thực tế (timestamp) */
    @Column(name = "actual_end_date")
    private Long actualEndDate;
}

