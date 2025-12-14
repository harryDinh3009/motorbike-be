package com.motorbikebe.repository.projection;

import java.math.BigDecimal;

public interface TotalRevenueProjection {
    BigDecimal getRentalAmount();
    BigDecimal getSurchargeAmount();
    BigDecimal getDiscountAmount();
}

