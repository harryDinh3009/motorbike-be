package com.translateai.dto.business.admin.employeeMng;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {

    /** ID nhân viên */
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

    /** Tên chi nhánh */
    private String branchName;

    /** Vai trò/Chức vụ */
    private String role;

    /** Trạng thái (1: Đang làm, 0: Nghỉ việc) */
    private Integer status;
}

