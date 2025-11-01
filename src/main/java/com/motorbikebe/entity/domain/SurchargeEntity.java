package com.motorbikebe.entity.domain;

import com.motorbikebe.entity.base.PrimaryEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entity quản lý phụ phí
 */
@Entity
@Table(name = "surcharge")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SurchargeEntity extends PrimaryEntity {

    /** ID hợp đồng */
    @Column(name = "contract_id", nullable = false, length = 36)
    private String contractId;

    /** Mô tả phụ phí (VD: Phạt trả xe trễ, Chi phí sửa chữa hư hỏng...) */
    @Column(name = "description", nullable = false, length = 500)
    private String description;

    /** Số tiền phụ phí */
    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    /** Ghi chú chi tiết về phụ phí */
    @Lob
    @Column(name = "notes")
    private String notes;
}

