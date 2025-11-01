package com.motorbikebe.dto.business.admin.employeeMng;

import com.motorbikebe.dto.common.PageableDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeSearchDTO extends PageableDTO {

    /** Tìm kiếm theo tên, SĐT, email */
    private String keyword;

    /** Lọc theo chi nhánh */
    private String branchId;

    /** Lọc theo chức vụ/vai trò */
    private String role;

    /** Lọc theo trạng thái (1: Đang làm, 0: Nghỉ việc) */
    private Integer status;
}

