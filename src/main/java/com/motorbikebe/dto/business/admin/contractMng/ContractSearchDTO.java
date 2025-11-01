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
    private Date startDateFrom;
    private Date startDateTo;
    private String pickupBranchId;
    private String returnBranchId;
    private ContractStatus status;
    private String source;
}
