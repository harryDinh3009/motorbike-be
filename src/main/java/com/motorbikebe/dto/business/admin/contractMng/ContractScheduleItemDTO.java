package com.motorbikebe.dto.business.admin.contractMng;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * DTO item hiển thị trong lịch đặt xe
 * Mỗi item đại diện cho 1 xe trong hợp đồng (contract_car)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContractScheduleItemDTO {
    /**
     * ID của contract_car (bản ghi xe trong hợp đồng)
     */
    private String contractCarId;
    
    /**
     * ID hợp đồng
     */
    private String contractId;
    
    /**
     * Mã hợp đồng (ví dụ: HD000123)
     */
    private String contractCode;
    
    /**
     * ID xe
     */
    private String carId;
    
    /**
     * Mẫu xe
     */
    private String carModel;
    
    /**
     * Biển số xe
     */
    private String licensePlate;
    
    /**
     * Tên khách hàng
     */
    private String customerName;
    
    /**
     * Số điện thoại khách hàng
     */
    private String customerPhone;
    
    /**
     * Ngày giờ bắt đầu thuê
     */
    private Date startDate;
    
    /**
     * Ngày giờ kết thúc thuê
     */
    private Date endDate;
    
    /**
     * Trạng thái hợp đồng
     */
    private String status;
    
    /**
     * ID chi nhánh nhận xe
     */
    private String pickupBranchId;
}

