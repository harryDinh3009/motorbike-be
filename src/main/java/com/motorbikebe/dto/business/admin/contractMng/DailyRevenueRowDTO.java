package com.motorbikebe.dto.business.admin.contractMng;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class DailyRevenueRowDTO {
    private LocalDate date;
    private int contractCount;
    private BigDecimal rentalAmount;
    private BigDecimal surchargeAmount;
    private BigDecimal discountAmount;
    private BigDecimal revenue;
}


