package com.motorbikebe.business.admin.contractMng.service;

import com.motorbikebe.dto.business.admin.contractMng.DailyRevenueReportRequestDTO;
import com.motorbikebe.dto.business.admin.contractMng.DailyRevenueRowDTO;
import com.motorbikebe.dto.business.admin.contractMng.ModelRentalReportRequestDTO;
import com.motorbikebe.dto.business.admin.contractMng.ModelRentalRowDTO;
import com.motorbikebe.dto.business.admin.contractMng.MonthlyRevenueReportRequestDTO;
import com.motorbikebe.dto.business.admin.contractMng.MonthlyRevenueRowDTO;

import java.util.List;

public interface ContractReportService {
    List<MonthlyRevenueRowDTO> getMonthlyRevenueData(MonthlyRevenueReportRequestDTO request);
    
    byte[] exportMonthlyRevenueReport(MonthlyRevenueReportRequestDTO request);
    
    List<DailyRevenueRowDTO> getDailyRevenueData(DailyRevenueReportRequestDTO request);
    
    byte[] exportDailyRevenueReport(DailyRevenueReportRequestDTO request);
    
    List<ModelRentalRowDTO> getModelRentalData(ModelRentalReportRequestDTO request);
    
    byte[] exportModelRentalReport(ModelRentalReportRequestDTO request);
}

