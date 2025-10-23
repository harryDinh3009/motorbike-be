package com.translateai.dto.business.admin.contractMng;

import com.translateai.dto.common.PageableDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerSearchDTO extends PageableDTO {

    /** Họ và tên (tìm kiếm theo tên) */
    private String fullName;

    /** Số điện thoại (tìm kiếm theo SĐT) */
    private String phoneNumber;

    /** Số CCCD/CMND (tìm kiếm theo số giấy tờ) */
    private String citizenId;
}

