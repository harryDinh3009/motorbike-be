package com.motorbikebe.dto.business.admin.contractMng;

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
public class MonthlyRevenueRowDTO {
    private int month;
    private BigDecimal rentalAmount;
    private BigDecimal surchargeAmount;
    private BigDecimal discountAmount;
    private BigDecimal revenue;
}

