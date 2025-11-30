package com.motorbikebe.business.admin.carMng.service;

import com.motorbikebe.dto.business.admin.carMng.AvailableCarReportRequestDTO;
import com.motorbikebe.dto.business.admin.carMng.RentableCarReportRequestDTO;

public interface CarReportService {
    byte[] exportAvailableCarsReport(AvailableCarReportRequestDTO request);
    
    byte[] exportRentableCarsReport(RentableCarReportRequestDTO request);
}

