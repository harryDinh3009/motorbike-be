package com.motorbikebe.dto.business.admin.contractMng;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * DTO request lấy dữ liệu lịch đặt xe
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContractScheduleRequestDTO {
    /**
     * ID chi nhánh thuê xe (null hoặc empty = tất cả chi nhánh)
     */
    private String branchId;
    
    /**
     * Trạng thái hợp đồng (null hoặc empty = tất cả trạng thái)
     * CONFIRMED, DELIVERED, RETURNED, COMPLETED, CANCELLED
     */
    private String status;
    
    /**
     * Ngày bắt đầu (đầu tháng)
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+07:00")
    private Date startDate;
    
    /**
     * Ngày kết thúc (cuối tháng)
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+07:00")
    private Date endDate;
}

