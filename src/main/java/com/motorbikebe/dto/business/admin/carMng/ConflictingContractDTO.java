package com.motorbikebe.dto.business.admin.carMng;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * DTO cho hợp đồng conflict với xe (dùng cho tooltip)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConflictingContractDTO {
    private String id;
    private String contractCode;
    private String customerName;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm", timezone = "GMT+07:00")
    private Date startDate;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm", timezone = "GMT+07:00")
    private Date endDate;
    private String status;
    private String statusNm;
}

