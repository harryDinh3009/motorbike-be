package com.motorbikebe.dto.business.admin.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardPerformanceDTO {
    private long completedContracts; // Số hợp đồng hoàn thành (theo completed_date)
    private BigDecimal totalRevenue; // Doanh thu (theo completed_date, = rental + surcharge - discount)
    private long totalCars; // Số xe cho thuê (theo start_date, status <> CANCELLED)
    private long newCustomers; // Số khách hàng mới (theo created_date)
}

