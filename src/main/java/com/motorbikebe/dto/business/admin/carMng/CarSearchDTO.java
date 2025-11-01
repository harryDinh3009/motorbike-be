package com.motorbikebe.dto.business.admin.carMng;

import com.motorbikebe.constant.enumconstant.CarStatus;
import com.motorbikebe.dto.common.PageableDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarSearchDTO extends PageableDTO {

    /** Tìm kiếm theo mẫu xe, biển số */
    private String keyword;

    /** Lọc theo chi nhánh */
    private String branchId;

    /** Lọc theo loại xe */
    private String carType;

    /** Lọc theo tình trạng xe */
    private String condition;

    /** Lọc theo trạng thái */
    private CarStatus status;
}

