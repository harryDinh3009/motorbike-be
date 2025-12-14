package com.motorbikebe.business.admin.dashboard.service;

import com.motorbikebe.dto.business.admin.dashboard.DashboardResponseDTO;
import com.motorbikebe.dto.business.admin.dashboard.DashboardRevenueChartDTO;

public interface DashboardService {
    DashboardResponseDTO getDashboard(String branchId);
    DashboardRevenueChartDTO getRevenueChart(String branchId, String period);
}

