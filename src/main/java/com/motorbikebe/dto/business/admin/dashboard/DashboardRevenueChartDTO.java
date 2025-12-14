package com.motorbikebe.dto.business.admin.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardRevenueChartDTO {
    private String period; // "7", "30", "year"
    private List<ChartDataPointDTO> data; // Dữ liệu chart
}


