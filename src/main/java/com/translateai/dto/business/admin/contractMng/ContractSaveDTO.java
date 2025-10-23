package com.translateai.dto.business.admin.contractMng;

import com.translateai.constant.enumconstant.ContractStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ContractSaveDTO {

    /** ID hợp đồng (null khi tạo mới) */
    private String id;

    /** ID xe */
    private String carId;

    /** ID khách hàng */
    private String customerId;

    /** Ngày bắt đầu thuê */
    private Long startDate;

    /** Ngày kết thúc thuê dự kiến */
    private Long endDate;

    /** Số ngày thuê */
    private Integer rentalDays;

    /** Giá thuê theo ngày */
    private BigDecimal dailyPrice;

    /** Tổng tiền thuê (chưa bao gồm phụ phí) */
    private BigDecimal totalAmount;

    /** Tổng phụ phí */
    private BigDecimal surchargeAmount;

    /** Tổng tiền cuối cùng */
    private BigDecimal finalAmount;

    /** Trạng thái hợp đồng */
    private ContractStatus status;

    /** Ghi chú */
    private String notes;

    /** Ngày trả xe thực tế */
    private Long actualEndDate;
}

