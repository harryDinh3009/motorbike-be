package com.motorbikebe.dto.business.admin.branchMng;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BranchDTO {

    /** ID chi nhánh */
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

