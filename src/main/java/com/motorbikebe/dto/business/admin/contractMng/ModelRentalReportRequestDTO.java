package com.motorbikebe.dto.business.admin.contractMng;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ModelRentalReportRequestDTO {
    
    private String branchId;
    
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+07:00")
    private Date startDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+07:00")
    private Date endDate;
}

