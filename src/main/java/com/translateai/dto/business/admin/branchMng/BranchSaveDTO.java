package com.translateai.dto.business.admin.branchMng;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BranchSaveDTO {

    /** ID chi nhánh (null khi tạo mới) */
    private String id;

    /** Tên chi nhánh */
    private String name;

    /** Số điện thoại */
    private String phoneNumber;

    /** Địa chỉ */
    private String address;

    /** Ghi chú */
    private String note;

    /** Trạng thái (1: Hoạt động, 0: Ngừng hoạt động) */
    private Integer status;
}

