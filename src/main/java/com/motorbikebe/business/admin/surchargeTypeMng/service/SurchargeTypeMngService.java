package com.motorbikebe.business.admin.surchargeTypeMng.service;

import com.motorbikebe.common.PageableObject;
import com.motorbikebe.dto.business.admin.surchargeTypeMng.SurchargeTypeDTO;
import com.motorbikebe.dto.business.admin.surchargeTypeMng.SurchargeTypeSaveDTO;
import com.motorbikebe.dto.business.admin.surchargeTypeMng.SurchargeTypeSearchDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface SurchargeTypeMngService {

    /**
     * Tìm kiếm danh sách phụ thu theo điều kiện
     */
    PageableObject<SurchargeTypeDTO> searchSurchargeTypes(SurchargeTypeSearchDTO searchDTO);

    /**
     * Lấy chi tiết một phụ thu
     */
    SurchargeTypeDTO getSurchargeTypeDetail(String id);

    /**
     * Thêm mới hoặc cập nhật phụ thu
     */
    Boolean saveSurchargeType(@Valid SurchargeTypeSaveDTO saveDTO);

    /**
     * Xóa phụ thu
     */
    Boolean deleteSurchargeType(String id);

    /**
     * Lấy tất cả phụ thu (dùng cho combobox)
     */
    List<SurchargeTypeDTO> getAllActiveSurchargeTypes();
}

