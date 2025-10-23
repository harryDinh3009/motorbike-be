package com.translateai.dto.business.admin.contractMng;

import com.translateai.constant.enumconstant.CarStatus;
import com.translateai.dto.common.PageableDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarSearchDTO extends PageableDTO {

    /** Tên xe (tìm kiếm theo tên) */
    private String name;

    /** Biển số xe (tìm kiếm theo biển số) */
    private String licensePlate;

    /** Trạng thái xe (lọc theo trạng thái) */
    private CarStatus status;
}

