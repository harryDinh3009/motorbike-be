package com.translateai.dto.business.admin.contractMng;

import com.translateai.constant.enumconstant.ContractStatus;
import com.translateai.dto.common.PageableDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContractSearchDTO extends PageableDTO {

    /** ID khách hàng (lọc theo khách hàng) */
    private String customerId;

    /** ID xe (lọc theo xe) */
    private String carId;

    /** Trạng thái hợp đồng (lọc theo trạng thái) */
    private ContractStatus status;

    /** Ngày bắt đầu từ (lọc theo khoảng thời gian) */
    private Long startDateFrom;

    /** Ngày bắt đầu đến (lọc theo khoảng thời gian) */
    private Long startDateTo;
}

