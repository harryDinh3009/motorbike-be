package com.translateai.business.admin.contractMng.service;

import com.translateai.common.PageableObject;
import com.translateai.dto.business.admin.contractMng.CarDTO;
import com.translateai.dto.business.admin.contractMng.CarSaveDTO;
import com.translateai.dto.business.admin.contractMng.CarSearchDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface CarMngService {

    /**
     * Tìm kiếm xe với phân trang
     *
     * @param searchDTO DTO tìm kiếm
     * @return PageableObject<CarDTO>
     */
    PageableObject<CarDTO> searchCars(CarSearchDTO searchDTO);

    /**
     * Lấy chi tiết xe
     *
     * @param id ID xe
     * @return CarDTO
     */
    CarDTO getCarDetail(String id);

    /**
     * Tạo mới hoặc cập nhật xe
     *
     * @param saveDTO DTO lưu xe
     * @return Boolean
     */
    Boolean saveCar(@Valid CarSaveDTO saveDTO);

    /**
     * Xóa xe
     *
     * @param id ID xe
     * @return Boolean
     */
    Boolean deleteCar(String id);

    /**
     * Lấy tất cả xe
     *
     * @return List<CarDTO>
     */
    List<CarDTO> getAllCars();
}

