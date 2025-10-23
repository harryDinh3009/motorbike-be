package com.translateai.dto.business.admin.contractMng;

import com.translateai.constant.enumconstant.ContractStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ContractDTO {

    /** Số thứ tự hiển thị */
    private Integer rowNum;

    /** ID hợp đồng */
    private String id;

    /** ID xe */
    private String carId;

    /** Tên xe */
    private String carName;

    /** Biển số xe */
    private String licensePlate;

    /** ID khách hàng */
    private String customerId;

    /** Tên khách hàng */
    private String customerName;

    /** Số điện thoại khách hàng */
    private String phoneNumber;

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

    /** Tên trạng thái hiển thị */
    private String statusNm;

    /** URL file hợp đồng PDF */
    private String contractFileUrl;

    /** Ghi chú */
    private String notes;

    /** Ngày trả xe thực tế */
    private Long actualEndDate;

    /** Danh sách phụ phí của hợp đồng */
    private List<SurchargeDTO> surcharges;
}

