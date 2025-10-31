package com.translateai.business.admin.surchargeTypeMng.web;

import com.translateai.business.admin.surchargeTypeMng.service.SurchargeTypeMngService;
import com.translateai.common.ApiResponse;
import com.translateai.common.ApiStatus;
import com.translateai.common.PageableObject;
import com.translateai.dto.business.admin.surchargeTypeMng.SurchargeTypeDTO;
import com.translateai.dto.business.admin.surchargeTypeMng.SurchargeTypeSaveDTO;
import com.translateai.dto.business.admin.surchargeTypeMng.SurchargeTypeSearchDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/a/surcharge-type-mng")
@RequiredArgsConstructor
public class SurchargeTypeMngController {

    private final SurchargeTypeMngService surchargeTypeMngService;

    /**
     * API tìm kiếm danh sách phụ thu
     *
     * @param searchDTO Điều kiện tìm kiếm
     * @return Danh sách phụ thu và pagination info
     */
    @PostMapping("/list")
    public ApiResponse<PageableObject<SurchargeTypeDTO>> searchSurchargeTypes(
            @RequestBody SurchargeTypeSearchDTO searchDTO) {
        PageableObject<SurchargeTypeDTO> pageableRes = surchargeTypeMngService.searchSurchargeTypes(searchDTO);
        return new ApiResponse<>(ApiStatus.SUCCESS, pageableRes);
    }

    /**
     * API lấy chi tiết một phụ thu
     *
     * @param id ID của phụ thu
     * @return Chi tiết phụ thu
     */
    @GetMapping("/detail/{id}")
    public ApiResponse<SurchargeTypeDTO> getSurchargeTypeDetail(@PathVariable String id) {
        SurchargeTypeDTO surchargeTypeDTO = surchargeTypeMngService.getSurchargeTypeDetail(id);
        return new ApiResponse<>(ApiStatus.SUCCESS, surchargeTypeDTO);
    }

    /**
     * API thêm mới hoặc cập nhật phụ thu
     *
     * @param saveDTO Dữ liệu phụ thu cần lưu
     * @return Kết quả lưu
     */
    @PostMapping("/save")
    public ApiResponse<Boolean> saveSurchargeType(@RequestBody SurchargeTypeSaveDTO saveDTO) {
        Boolean result = surchargeTypeMngService.saveSurchargeType(saveDTO);
        return new ApiResponse<>(ApiStatus.SUCCESS, result);
    }

    /**
     * API xóa phụ thu
     *
     * @param id ID của phụ thu cần xóa
     * @return Kết quả xóa
     */
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Boolean> deleteSurchargeType(@PathVariable String id) {
        Boolean result = surchargeTypeMngService.deleteSurchargeType(id);
        return new ApiResponse<>(ApiStatus.SUCCESS, result);
    }

    /**
     * API lấy tất cả phụ thu đang active (dùng cho combobox)
     *
     * @return Danh sách phụ thu active
     */
    @GetMapping("/all-active")
    public ApiResponse<List<SurchargeTypeDTO>> getAllActiveSurchargeTypes() {
        List<SurchargeTypeDTO> surchargeTypes = surchargeTypeMngService.getAllActiveSurchargeTypes();
        return new ApiResponse<>(ApiStatus.SUCCESS, surchargeTypes);
    }
}

