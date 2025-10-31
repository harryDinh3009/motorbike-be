package com.translateai.business.admin.employeeMng.service;

import com.translateai.common.PageableObject;
import com.translateai.dto.business.admin.employeeMng.EmployeeDTO;
import com.translateai.dto.business.admin.employeeMng.EmployeeSaveDTO;
import com.translateai.dto.business.admin.employeeMng.EmployeeSearchDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface EmployeeMngService {

    /**
     * Tìm kiếm nhân viên với phân trang
     *
     * @param searchDTO DTO tìm kiếm
     * @return PageableObject<EmployeeDTO>
     */
    PageableObject<EmployeeDTO> searchEmployees(EmployeeSearchDTO searchDTO);

    /**
     * Lấy chi tiết nhân viên
     *
     * @param id ID nhân viên
     * @return EmployeeDTO
     */
    EmployeeDTO getEmployeeDetail(String id);

    /**
     * Tạo mới hoặc cập nhật nhân viên
     *
     * @param saveDTO DTO lưu nhân viên
     * @return Boolean
     */
    Boolean saveEmployee(@Valid EmployeeSaveDTO saveDTO);

    /**
     * Xóa nhân viên
     *
     * @param id ID nhân viên
     * @return Boolean
     */
    Boolean deleteEmployee(String id);

    /**
     * Lấy tất cả nhân viên theo chi nhánh
     *
     * @param branchId ID chi nhánh
     * @return List<EmployeeDTO>
     */
    List<EmployeeDTO> getEmployeesByBranch(String branchId);
}

