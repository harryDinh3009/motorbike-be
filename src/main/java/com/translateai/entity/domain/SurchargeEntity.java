package com.translateai.entity.domain;

import com.translateai.entity.base.PrimaryEntity;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private ContractEntity contract;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}

