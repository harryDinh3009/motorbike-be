package com.motorbikebe.business.admin.employeeMng.web;

import com.motorbikebe.business.admin.employeeMng.service.EmployeeMngService;
import com.motorbikebe.common.ApiResponse;
import com.motorbikebe.common.ApiStatus;
import com.motorbikebe.common.PageableObject;
import com.motorbikebe.dto.business.admin.employeeMng.EmployeeDTO;
import com.motorbikebe.dto.business.admin.employeeMng.EmployeeSaveDTO;
import com.motorbikebe.dto.business.admin.employeeMng.EmployeeSearchDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/a/employee-mng")
@RequiredArgsConstructor
public class EmployeeMngController {

    private final EmployeeMngService employeeMngService;

    /**
     * Tìm kiếm nhân viên với phân trang
     *
     * @param searchDTO DTO tìm kiếm
     * @return PageableObject<EmployeeDTO>
     */
    @PostMapping("/list")
    public ApiResponse<PageableObject<EmployeeDTO>> searchEmployees(@RequestBody EmployeeSearchDTO searchDTO) {
        PageableObject<EmployeeDTO> pageableRes = employeeMngService.searchEmployees(searchDTO);
        return new ApiResponse<>(ApiStatus.SUCCESS, pageableRes);
    }

    /**
     * Lấy chi tiết nhân viên
     *
     * @param id ID nhân viên
     * @return EmployeeDTO
     */
    @GetMapping("/detail")
    public ApiResponse<EmployeeDTO> getEmployeeDetail(@RequestParam("id") String id) {
        EmployeeDTO response = employeeMngService.getEmployeeDetail(id);
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }

    /**
     * Tạo mới hoặc cập nhật nhân viên
     *
     * @param saveDTO DTO lưu nhân viên
     * @return Boolean
     */
    @PostMapping("/save")
    public ApiResponse<Boolean> saveEmployee(@RequestBody EmployeeSaveDTO saveDTO) {
        Boolean response = employeeMngService.saveEmployee(saveDTO);
        return new ApiResponse<>(ApiStatus.CREATED, response);
    }

    /**
     * Xóa nhân viên
     *
     * @param id ID nhân viên
     * @return Boolean
     */
    @DeleteMapping("/delete")
    public ApiResponse<Boolean> deleteEmployee(@RequestParam("id") String id) {
        Boolean response = employeeMngService.deleteEmployee(id);
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }

    /**
     * Lấy danh sách nhân viên theo chi nhánh
     *
     * @param branchId ID chi nhánh
     * @return List<EmployeeDTO>
     */
    @GetMapping("/by-branch")
    public ApiResponse<List<EmployeeDTO>> getEmployeesByBranch(@RequestParam("branchId") String branchId) {
        List<EmployeeDTO> response = employeeMngService.getEmployeesByBranch(branchId);
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }
}

