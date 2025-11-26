package com.motorbikebe.dto.business.admin.contractMng;

import com.motorbikebe.constant.enumconstant.ContractStatus;
import com.motorbikebe.dto.common.PageableDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * DTO tìm kiếm hợp đồng
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContractSearchDTO extends PageableDTO {
    // Tìm theo tên khách, SDT, số hợp đồng, biển số xe
    private String keyword;
    
    // Filter
    private Date startDateFrom;  // Ngày thuê từ
    private Date startDateTo;    // Ngày thuê đến
    private Date endDateFrom;    // Ngày trả từ
    private Date endDateTo;      // Ngày trả đến
    private String pickupBranchId;
    private String returnBranchId;
    private ContractStatus status;
    private String source;
}
