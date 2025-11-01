package com.motorbikebe.business.admin.branchMng.web;

import com.motorbikebe.business.admin.branchMng.service.BranchMngService;
import com.motorbikebe.common.ApiResponse;
import com.motorbikebe.common.ApiStatus;
import com.motorbikebe.common.PageableObject;
import com.motorbikebe.dto.business.admin.branchMng.BranchDTO;
import com.motorbikebe.dto.business.admin.branchMng.BranchSaveDTO;
import com.motorbikebe.dto.business.admin.branchMng.BranchSearchDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/a/branch-mng")
@RequiredArgsConstructor
public class BranchMngController {

    private final BranchMngService branchMngService;

    /**
     * Tìm kiếm chi nhánh với phân trang
     *
     * @param searchDTO DTO tìm kiếm
     * @return PageableObject<BranchDTO>
     */
    @PostMapping("/list")
    public ApiResponse<PageableObject<BranchDTO>> searchBranches(@RequestBody BranchSearchDTO searchDTO) {
        PageableObject<BranchDTO> pageableRes = branchMngService.searchBranches(searchDTO);
        return new ApiResponse<>(ApiStatus.SUCCESS, pageableRes);
    }

    /**
     * Lấy chi tiết chi nhánh
     *
     * @param id ID chi nhánh
     * @return BranchDTO
     */
    @GetMapping("/detail")
    public ApiResponse<BranchDTO> getBranchDetail(@RequestParam("id") String id) {
        BranchDTO response = branchMngService.getBranchDetail(id);
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }

    /**
     * Tạo mới hoặc cập nhật chi nhánh
     *
     * @param saveDTO DTO lưu chi nhánh
     * @return Boolean
     */
    @PostMapping("/save")
    public ApiResponse<Boolean> saveBranch(@RequestBody BranchSaveDTO saveDTO) {
        Boolean response = branchMngService.saveBranch(saveDTO);
        return new ApiResponse<>(ApiStatus.CREATED, response);
    }

    /**
     * Xóa chi nhánh
     *
     * @param id ID chi nhánh
     * @return Boolean
     */
    @DeleteMapping("/delete")
    public ApiResponse<Boolean> deleteBranch(@RequestParam("id") String id) {
        Boolean response = branchMngService.deleteBranch(id);
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }

    /**
     * Lấy tất cả chi nhánh đang hoạt động (để hiển thị dropdown)
     *
     * @return List<BranchDTO>
     */
    @GetMapping("/active-list")
    public ApiResponse<List<BranchDTO>> getAllActiveBranches() {
        List<BranchDTO> response = branchMngService.getAllActiveBranches();
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }
}

