package com.motorbikebe.entity.domain;

import com.motorbikebe.entity.base.PrimaryEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Entity quản lý lịch sử thanh toán của hợp đồng
 */
@Entity
@Table(name = "payment_transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicUpdate
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentTransactionEntity extends PrimaryEntity {

    /** Mã thanh toán (tự generate) */
    @Column(name = "transaction_code", length = 50, unique = true)
    String transactionCode;

    /** ID hợp đồng */
    @Column(name = "contract_id", nullable = false, length = 36)
    String contractId;

    /** Phương thức thanh toán */
    @Column(name = "payment_method", length = 100)
    String paymentMethod;

    /** Số tiền thanh toán */
    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    BigDecimal amount;

    /** Ngày thanh toán */
    @Column(name = "payment_date", nullable = false)
    Date paymentDate;

    /** Nhân viên xử lý */
    @Column(name = "employee_id", length = 36)
    String employeeId;

    /** Ghi chú */
    @Lob
    @Column(name = "notes")
    String notes;

    /** Trạng thái: SUCCESS, CANCELLED */
    @Column(name = "status", length = 20)
    String status;
}

