package com.motorbikebe.repository.projection;

import java.math.BigDecimal;

public interface TopCarRentalProjection {
    String getModel();
    Long getRentalCount();
    BigDecimal getRevenue();
}

