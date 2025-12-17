package com.motorbikebe.business.admin.brandMng.web;

import com.motorbikebe.business.admin.brandMng.service.BrandMngService;
import com.motorbikebe.common.ApiResponse;
import com.motorbikebe.common.ApiStatus;
import com.motorbikebe.common.PageableObject;
import com.motorbikebe.dto.business.admin.brandMng.BrandDTO;
import com.motorbikebe.dto.business.admin.brandMng.BrandSaveDTO;
import com.motorbikebe.dto.business.admin.brandMng.BrandSearchDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/a/brand-mng")
@RequiredArgsConstructor
public class BrandMngController {

    private final BrandMngService brandMngService;

    /**
     * Tìm kiếm hãng xe với phân trang
     *
     * @param searchDTO DTO tìm kiếm
     * @return PageableObject<BrandDTO>
     */
    @PostMapping("/list")
    public ApiResponse<PageableObject<BrandDTO>> searchBrands(@RequestBody BrandSearchDTO searchDTO) {
        PageableObject<BrandDTO> pageableRes = brandMngService.searchBrands(searchDTO);
        return new ApiResponse<>(ApiStatus.SUCCESS, pageableRes);
    }

    /**
     * Lấy chi tiết hãng xe
     *
     * @param id ID hãng xe
     * @return BrandDTO
     */
    @GetMapping("/detail")
    public ApiResponse<BrandDTO> getBrandDetail(@RequestParam("id") String id) {
        BrandDTO response = brandMngService.getBrandDetail(id);
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }

    /**
     * Tạo mới hoặc cập nhật hãng xe
     *
     * @param saveDTO DTO lưu hãng xe
     * @return Boolean
     */
    @PostMapping("/save")
    public ApiResponse<Boolean> saveBrand(@RequestBody BrandSaveDTO saveDTO) {
        Boolean response = brandMngService.saveBrand(saveDTO);
        return new ApiResponse<>(ApiStatus.CREATED, response);
    }

    /**
     * Xóa hãng xe
     *
     * @param id ID hãng xe
     * @return Boolean
     */
    @DeleteMapping("/delete")
    public ApiResponse<Boolean> deleteBrand(@RequestParam("id") String id) {
        Boolean response = brandMngService.deleteBrand(id);
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }

    /**
     * Lấy tất cả hãng xe (để hiển thị dropdown)
     *
     * @return List<BrandDTO>
     */
    @GetMapping("/all")
    public ApiResponse<List<BrandDTO>> getAllBrands() {
        List<BrandDTO> response = brandMngService.getAllBrands();
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }
}

