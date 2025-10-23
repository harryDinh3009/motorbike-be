package com.translateai.business.admin.contractMng.web;

import com.translateai.business.admin.contractMng.service.CarMngService;
import com.translateai.common.ApiResponse;
import com.translateai.common.ApiStatus;
import com.translateai.common.PageableObject;
import com.translateai.dto.business.admin.contractMng.CarDTO;
import com.translateai.dto.business.admin.contractMng.CarSaveDTO;
import com.translateai.dto.business.admin.contractMng.CarSearchDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/a/car-mng")
@RequiredArgsConstructor
public class CarMngController {

    private final CarMngService carMngService;

    /**
     * Tìm kiếm xe với phân trang
     *
     * @param searchDTO DTO tìm kiếm
     * @return PageableObject<CarDTO>
     */
    @GetMapping("/list")
    public ApiResponse<PageableObject<CarDTO>> searchCars(CarSearchDTO searchDTO) {
        PageableObject<CarDTO> pageableRes = carMngService.searchCars(searchDTO);
        return new ApiResponse<>(ApiStatus.SUCCESS, pageableRes);
    }

    /**
     * Lấy chi tiết xe
     *
     * @param id ID xe
     * @return CarDTO
     */
    @GetMapping("/detail")
    public ApiResponse<CarDTO> getCarDetail(@RequestParam("id") String id) {
        CarDTO response = carMngService.getCarDetail(id);
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }

    /**
     * Tạo mới hoặc cập nhật xe
     *
     * @param saveDTO DTO lưu xe
     * @return Boolean
     */
    @PostMapping("/save")
    public ApiResponse<Boolean> saveCar(@RequestBody CarSaveDTO saveDTO) {
        Boolean response = carMngService.saveCar(saveDTO);
        return new ApiResponse<>(ApiStatus.CREATED, response);
    }

    /**
     * Xóa xe
     *
     * @param id ID xe
     * @return Boolean
     */
    @DeleteMapping("/delete")
    public ApiResponse<Boolean> deleteCar(@RequestParam("id") String id) {
        Boolean response = carMngService.deleteCar(id);
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }

    /**
     * Lấy tất cả xe
     *
     * @return List<CarDTO>
     */
    @GetMapping("/all")
    public ApiResponse<List<CarDTO>> getAllCars() {
        List<CarDTO> response = carMngService.getAllCars();
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }
}

