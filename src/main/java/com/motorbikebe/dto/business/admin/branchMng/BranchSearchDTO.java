package com.motorbikebe.dto.business.admin.branchMng;

import com.motorbikebe.dto.common.PageableDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BranchSearchDTO extends PageableDTO {

    /** Tìm kiếm theo tên chi nhánh hoặc số điện thoại */
    private String keyword;

    /** Lọc theo trạng thái (1: Hoạt động, 0: Ngừng hoạt động) */
    private Integer status;
}

