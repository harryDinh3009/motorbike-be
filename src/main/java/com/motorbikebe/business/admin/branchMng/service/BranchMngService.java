package com.motorbikebe.business.admin.branchMng.service;

import com.motorbikebe.common.PageableObject;
import com.motorbikebe.dto.business.admin.branchMng.BranchDTO;
import com.motorbikebe.dto.business.admin.branchMng.BranchSaveDTO;
import com.motorbikebe.dto.business.admin.branchMng.BranchSearchDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface BranchMngService {

    /**
     * Tìm kiếm chi nhánh với phân trang
     *
     * @param searchDTO DTO tìm kiếm
     * @return PageableObject<BranchDTO>
     */
    PageableObject<BranchDTO> searchBranches(BranchSearchDTO searchDTO);

    /**
     * Lấy chi tiết chi nhánh
     *
     * @param id ID chi nhánh
     * @return BranchDTO
     */
    BranchDTO getBranchDetail(String id);

    /**
     * Tạo mới hoặc cập nhật chi nhánh
     *
     * @param saveDTO DTO lưu chi nhánh
     * @return Boolean
     */
    Boolean saveBranch(@Valid BranchSaveDTO saveDTO);

    /**
     * Xóa chi nhánh
     *
     * @param id ID chi nhánh
     * @return Boolean
     */
    Boolean deleteBranch(String id);

    /**
     * Lấy tất cả chi nhánh (active)
     *
     * @return List<BranchDTO>
     */
    List<BranchDTO> getAllActiveBranches();

    /**
     * Lấy chi nhánh của user đang đăng nhập
     *
     * @return BranchDTO
     */
    BranchDTO getBranchByCurrentUser();
}

