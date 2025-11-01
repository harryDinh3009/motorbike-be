package com.motorbikebe.dto.business.admin.customerMng;

import com.motorbikebe.dto.common.PageableDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerSearchDTO extends PageableDTO {

    /** Tìm kiếm theo tên, SĐT, email */
    private String keyword;

    /** Lọc theo quốc gia */
    private String country;
}

