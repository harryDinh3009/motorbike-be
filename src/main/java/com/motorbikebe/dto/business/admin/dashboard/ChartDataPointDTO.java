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
public class ChartDataPointDTO {
    private String label; // Ngày hoặc tháng
    private BigDecimal revenue; // Doanh thu
}


