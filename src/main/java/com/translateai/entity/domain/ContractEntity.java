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

    @Column(name = "car_id", nullable = false, length = 36)
    private String carId;

    @Column(name = "customer_id", nullable = false, length = 36)
    private String customerId;

    @Column(name = "start_date", nullable = false)
    private Long startDate;

    @Column(name = "end_date", nullable = false)
    private Long endDate;

    @Column(name = "rental_days", nullable = false)
    private Integer rentalDays;

    @Column(name = "daily_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal dailyPrice;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "surcharge_amount", precision = 15, scale = 2)
    private BigDecimal surchargeAmount = BigDecimal.ZERO;

    @Column(name = "final_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal finalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ContractStatus status = ContractStatus.NEW;

    @Column(name = "contract_file_url", length = 500)
    private String contractFileUrl;

    @Lob
    @Column(name = "notes")
    private String notes;

    @Column(name = "actual_end_date")
    private Long actualEndDate;
}

