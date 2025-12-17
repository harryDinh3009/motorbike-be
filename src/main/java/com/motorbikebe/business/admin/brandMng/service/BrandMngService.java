package com.motorbikebe.business.admin.brandMng.service;

import com.motorbikebe.common.PageableObject;
import com.motorbikebe.dto.business.admin.brandMng.BrandDTO;
import com.motorbikebe.dto.business.admin.brandMng.BrandSaveDTO;
import com.motorbikebe.dto.business.admin.brandMng.BrandSearchDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface BrandMngService {

    /**
     * Tìm kiếm hãng xe với phân trang
     *
     * @param searchDTO DTO tìm kiếm
     * @return PageableObject<BrandDTO>
     */
    PageableObject<BrandDTO> searchBrands(BrandSearchDTO searchDTO);

    /**
     * Lấy chi tiết hãng xe
     *
     * @param id ID hãng xe
     * @return BrandDTO
     */
    BrandDTO getBrandDetail(String id);

    /**
     * Tạo mới hoặc cập nhật hãng xe
     *
     * @param saveDTO DTO lưu hãng xe
     * @return Boolean
     */
    Boolean saveBrand(@Valid BrandSaveDTO saveDTO);

    /**
     * Xóa hãng xe
     *
     * @param id ID hãng xe
     * @return Boolean
     */
    Boolean deleteBrand(String id);

    /**
     * Lấy tất cả hãng xe (để hiển thị dropdown)
     *
     * @return List<BrandDTO>
     */
    List<BrandDTO> getAllBrands();
}

