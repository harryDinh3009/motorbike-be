package com.motorbikebe.dto.business.admin.contractMng;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO filter export báo cáo doanh thu theo tháng
 */
@Getter
@Setter
public class MonthlyRevenueReportRequestDTO {
    /** Năm cần báo cáo */
    private Integer year;
    /** Chi nhánh (optional) */
    private String branchId;
}

