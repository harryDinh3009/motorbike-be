package com.motorbikebe.dto.business.admin.brandMng;

import com.motorbikebe.dto.common.PageableDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BrandSearchDTO extends PageableDTO {

    /** Tìm kiếm theo tên hãng xe hoặc mô tả */
    private String keyword;
}

