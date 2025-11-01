package com.translateai.entity.domain;

import com.translateai.constant.enumconstant.ContractStatus;
import com.translateai.entity.base.PrimaryEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Entity quản lý hợp đồng cho thuê xe (đã nâng cấp)
 */
@Entity
@Table(name = "contract")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicUpdate
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractEntity extends PrimaryEntity {

    /** Mã hợp đồng (tự generate) */
    @Column(name = "contract_code", length = 50, unique = true)
    String contractCode;

    /** ID khách hàng thuê xe */
    @Column(name = "customer_id", nullable = false, length = 36)
    String customerId;

    /** Nguồn khách hàng (Walk-in, Facebook, Hotline, Zalo) */
    @Column(name = "source", length = 50)
    String source;

    /** Ngày thuê */
    @Column(name = "start_date", nullable = false)
    Date startDate;

    /** Ngày trả dự kiến */
    @Column(name = "end_date", nullable = false)
    Date endDate;

    /** Chi nhánh thuê xe */
    @Column(name = "pickup_branch_id", length = 36)
    String pickupBranchId;

    /** Chi nhánh trả xe */
    @Column(name = "return_branch_id", length = 36)
    String returnBranchId;

    /** Địa điểm giao xe */
    @Column(name = "pickup_address", length = 500)
    String pickupAddress;

    /** Địa điểm trả xe */
    @Column(name = "return_address", length = 500)
    String returnAddress;

    /** Cần vận chuyển giao xe tận nơi */
    @Column(name = "need_pickup_delivery")
    Boolean needPickupDelivery;

    /** Cần vận chuyển nhận xe tận nơi */
    @Column(name = "need_return_delivery")
    Boolean needReturnDelivery;

    /** Ghi chú hợp đồng */
    @Lob
    @Column(name = "notes")
    String notes;

    /** Tổng tiền thuê xe (tất cả các xe) */
    @Column(name = "total_rental_amount", precision = 15, scale = 2)
    BigDecimal totalRentalAmount;

    /** Tổng phụ thu */
    @Column(name = "total_surcharge", precision = 15, scale = 2)
    BigDecimal totalSurcharge;

    /** Loại giảm giá (PERCENTAGE / AMOUNT) */
    @Column(name = "discount_type", length = 20)
    String discountType;

    /** Giá trị giảm giá */
    @Column(name = "discount_value", precision = 15, scale = 2)
    BigDecimal discountValue;

    /** Tiền giảm giá thực tế */
    @Column(name = "discount_amount", precision = 15, scale = 2)
    BigDecimal discountAmount;

    /** Tiền đặt cọc */
    @Column(name = "deposit_amount", precision = 15, scale = 2)
    BigDecimal depositAmount;

    /** Tổng tiền cuối cùng phải trả */
    @Column(name = "final_amount", precision = 15, scale = 2)
    BigDecimal finalAmount;

    /** Đã thanh toán */
    @Column(name = "paid_amount", precision = 15, scale = 2)
    BigDecimal paidAmount;

    /** Còn lại phải thu */
    @Column(name = "remaining_amount", precision = 15, scale = 2)
    BigDecimal remainingAmount;

    /** Trạng thái hợp đồng */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    ContractStatus status;

    /** Nhân viên giao xe */
    @Column(name = "delivery_employee_id", length = 36)
    String deliveryEmployeeId;

    /** Thời gian giao xe thực tế */
    @Column(name = "delivery_time")
    Date deliveryTime;

    /** Nhân viên nhận xe */
    @Column(name = "return_employee_id", length = 36)
    String returnEmployeeId;

    /** Thời gian nhận xe thực tế */
    @Column(name = "return_time")
    Date returnTime;

    /** Ngày đóng hợp đồng */
    @Column(name = "completed_date")
    Date completedDate;
}
