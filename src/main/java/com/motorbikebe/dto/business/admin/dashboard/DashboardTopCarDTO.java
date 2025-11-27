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
public class DashboardTopCarDTO {
    private Integer rank;              // Số thứ tự
    private String model;               // Mẫu xe
    private Long rentalCount;            // Số lượt thuê
    private BigDecimal revenue;          // Doanh thu mang lại
}

