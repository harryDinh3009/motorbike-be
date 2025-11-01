package com.motorbikebe.dto.business.admin.employeeMng;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class EmployeeSaveDTO {

    /** ID nhân viên (null khi tạo mới) */
    private String id;

    /** Họ và tên */
    private String fullName;

    /** Số điện thoại */
    private String phoneNumber;

    /** Email */
    private String email;

    /** Ngày sinh */
    private Date dateOfBirth;

    /** Giới tính */
    private String gender;

    /** Địa chỉ */
    private String address;

    /** ID chi nhánh */
    private String branchId;

    /** Vai trò/Chức vụ */
    private String role;

    /** Trạng thái (1: Đang làm, 0: Nghỉ việc) */
    private Integer status;
}

