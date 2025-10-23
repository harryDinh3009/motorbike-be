package com.translateai.dto.business.admin.contractMng;

import com.translateai.constant.enumconstant.ContractStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ContractDTO {

    /** Constructor cho native query - 18 parameters (không bao gồm statusNm và surcharges) */
    public ContractDTO(Long rowNum, String id, String carId, String carName, String licensePlate,
                       String customerId, String customerName, String phoneNumber,
                       Long startDate, Long endDate, Integer rentalDays,
                       BigDecimal dailyPrice, BigDecimal totalAmount, BigDecimal surchargeAmount,
                       BigDecimal finalAmount, String status, String notes, Long actualEndDate) {
        this.rowNum = rowNum != null ? rowNum.intValue() : null;
        this.id = id;
        this.carId = carId;
        this.carName = carName;
        this.licensePlate = licensePlate;
        this.customerId = customerId;
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.rentalDays = rentalDays;
        this.dailyPrice = dailyPrice;
        this.totalAmount = totalAmount;
        this.surchargeAmount = surchargeAmount;
        this.finalAmount = finalAmount;
        this.status = ContractStatus.valueOf(status);
        this.notes = notes;
        this.actualEndDate = actualEndDate;
    }

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

    /** Ghi chú */
    private String notes;

    /** Ngày trả xe thực tế */
    private Long actualEndDate;

    /** Danh sách phụ phí của hợp đồng */
    private List<SurchargeDTO> surcharges;
}

