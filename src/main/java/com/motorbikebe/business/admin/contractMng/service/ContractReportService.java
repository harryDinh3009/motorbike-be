package com.motorbikebe.business.admin.contractMng.service;

import com.motorbikebe.dto.business.admin.contractMng.MonthlyRevenueReportRequestDTO;

public interface ContractReportService {
    byte[] exportMonthlyRevenueReport(MonthlyRevenueReportRequestDTO request);
}

