package com.motorbikebe.dto.business.admin.contractMng;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.motorbikebe.dto.common.PageableDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * DTO tìm kiếm hợp đồng giao/nhận xe (tối ưu)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPickupSearchDTO extends PageableDTO {
    // Tìm theo mã hợp đồng, tên khách hàng, số điện thoại
    private String keyword;
    
    // Filter
    private String branchId;  // pickupBranchId cho delivery, returnBranchId cho pickup
    
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+07:00")
    private Date dateFrom;     // startDateFrom cho delivery, endDateFrom cho pickup
    
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+07:00")
    private Date dateTo;       // startDateTo cho delivery, endDateTo cho pickup
    private String status;     // "all" | "delivered"/"received" | "not_delivered"/"not_received"
}

