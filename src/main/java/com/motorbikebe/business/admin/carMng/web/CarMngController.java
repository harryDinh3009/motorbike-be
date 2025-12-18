package com.motorbikebe.business.admin.carMng.web;

import com.motorbikebe.business.admin.carMng.excel.CarExcelService;
import com.motorbikebe.business.admin.carMng.service.CarModelService;
import com.motorbikebe.business.admin.carMng.service.CarMngService;
import com.motorbikebe.business.admin.carMng.service.CarReportService;
import com.motorbikebe.common.ApiResponse;
import com.motorbikebe.common.ApiStatus;
import com.motorbikebe.common.PageableObject;
import com.motorbikebe.constant.classconstant.CarConstants;
import com.motorbikebe.constant.enumconstant.CarStatus;
import com.motorbikebe.dto.business.admin.carMng.AvailableCarDTO;
import com.motorbikebe.dto.business.admin.carMng.*;
import com.motorbikebe.dto.business.admin.carMng.CarModelInfoDTO;
import com.motorbikebe.dto.business.admin.carMng.ConflictingContractDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/a/car-mng")
@RequiredArgsConstructor
public class CarMngController {

    private final CarMngService carMngService;
    private final CarExcelService carExcelService;
    private final CarModelService carModelService;
    private final CarReportService carReportService;

    /**
     * Tìm kiếm xe với phân trang
     *
     * @param searchDTO DTO tìm kiếm
     * @return PageableObject<CarDTO>
     */
    @PostMapping("/list")
    public ApiResponse<PageableObject<CarDTO>> searchCars(@RequestBody CarSearchDTO searchDTO) {
        PageableObject<CarDTO> pageableRes = carMngService.searchCars(searchDTO);
        return new ApiResponse<>(ApiStatus.SUCCESS, pageableRes);
    }

    /**
     * Tìm kiếm xe có sẵn với phân trang
     * - Lọc theo chi nhánh: nếu không truyền branchId thì mặc định lấy chi nhánh của người đang đăng nhập
     * - Nếu truyền startDate và endDate: kiểm tra xe có trong hợp đồng nào có thời gian trùng lặp không
     *   + Nếu trùng: chuyển status về NOT_AVAILABLE
     *   + Nếu không trùng: giữ nguyên status của xe
     *
     * @param searchDTO DTO tìm kiếm (bao gồm startDate và endDate)
     * @return PageableObject<CarDTO>
     */
    @PostMapping("/list-available")
    public ApiResponse<PageableObject<CarDTO>> searchAvailableCars(@RequestBody CarSearchDTO searchDTO) {
        PageableObject<CarDTO> pageableRes = carMngService.searchAvailableCars(searchDTO);
        return new ApiResponse<>(ApiStatus.SUCCESS, pageableRes);
    }

    /**
     * Tìm kiếm xe khả dụng (lightweight) - chỉ trả về các field cần thiết
     * Dùng cho màn chọn xe khi tạo hợp đồng để tối ưu hiệu suất
     * - Lọc theo chi nhánh: nếu không truyền branchId thì mặc định lấy chi nhánh của người đang đăng nhập
     * - Nếu truyền startDate và endDate: kiểm tra xe có trong hợp đồng nào có thời gian trùng lặp không
     *   + Nếu trùng: chuyển status về NOT_AVAILABLE
     *   + Nếu không trùng: giữ nguyên status của xe
     *
     * @param searchDTO DTO tìm kiếm (bao gồm startDate và endDate)
     * @return PageableObject<AvailableCarDTO>
     */
    @PostMapping("/list-available-light")
    public ApiResponse<PageableObject<AvailableCarDTO>> searchAvailableCarsLight(@RequestBody CarSearchDTO searchDTO) {
        PageableObject<AvailableCarDTO> pageableRes = carMngService.searchAvailableCarsLight(searchDTO);
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
     * Lấy danh sách hợp đồng conflict với xe trong khoảng thời gian
     * Dùng để hiển thị tooltip khi xe không khả dụng
     *
     * @param carId ID xe
     * @param startDate Ngày bắt đầu (ISO string)
     * @param endDate Ngày kết thúc (ISO string)
     * @return List<ConflictingContractDTO>
     */
    @GetMapping("/{carId}/conflicting-contracts")
    public ApiResponse<List<ConflictingContractDTO>> getConflictingContracts(
            @PathVariable String carId,
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        List<ConflictingContractDTO> contracts = carMngService.getConflictingContracts(carId, startDate, endDate);
        return new ApiResponse<>(ApiStatus.SUCCESS, contracts);
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

    @GetMapping("/generate-vehicle-code")
    public ApiResponse<String> generateNextVehicleCode() {
        String nextCode = carMngService.generateNextVehicleCode();
        return new ApiResponse<>(ApiStatus.SUCCESS, nextCode);
    }

    /**
     * Upload ảnh xe
     *
     * @param carId ID xe
     * @param file File ảnh
     * @return URL ảnh
     */
    @PostMapping(value = "/upload-image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResponse<String> uploadCarImage(
            @RequestParam("carId") String carId,
            @RequestParam("file") MultipartFile file) {
        String imageUrl = carMngService.uploadCarImage(carId, file);
        return new ApiResponse<>(ApiStatus.SUCCESS, imageUrl);
    }

    /**
     * Lấy danh sách mẫu xe
     *
     * @return List<String>
     */
    @GetMapping("/car-models")
    public ApiResponse<List<String>> getCarModels() {
        return new ApiResponse<>(ApiStatus.SUCCESS, carModelService.getActiveModelNames());
    }

    /**
     * Lấy toàn bộ thông tin mẫu xe (bao gồm trạng thái) cho admin quản lý
     */
    @GetMapping("/car-models/manage")
    public ApiResponse<List<CarModelDTO>> getCarModelsForManage() {
        return new ApiResponse<>(ApiStatus.SUCCESS, carModelService.getAllCarModels());
    }

    /**
     * Lấy thông tin model để populate vào car
     */
    @GetMapping("/car-model-info")
    public ApiResponse<CarModelInfoDTO> getCarModelInfo(@RequestParam("modelName") String modelName) {
        CarModelInfoDTO info = carModelService.getCarModelInfo(modelName);
        return new ApiResponse<>(ApiStatus.SUCCESS, info);
    }

    /**
     * Tạo mẫu xe mới
     */
    @PostMapping("/car-models")
    public ApiResponse<CarModelDTO> createCarModel(@Valid @RequestBody CarModelSaveDTO saveDTO) {
        CarModelDTO response = carModelService.createCarModel(saveDTO);
        return new ApiResponse<>(ApiStatus.CREATED, response);
    }

    /**
     * Cập nhật mẫu xe
     */
    @PutMapping("/car-models/{id}")
    public ApiResponse<CarModelDTO> updateCarModel(@PathVariable String id,
                                                   @Valid @RequestBody CarModelSaveDTO saveDTO) {
        CarModelDTO response = carModelService.updateCarModel(id, saveDTO);
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }

    /**
     * Xóa mẫu xe
     */
    @DeleteMapping("/car-models/{id}")
    public ApiResponse<Boolean> deleteCarModel(@PathVariable String id) {
        Boolean response = carModelService.deleteCarModel(id);
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }

    /**
     * Lấy danh sách loại xe
     *
     * @return List<String>
     */
    @GetMapping("/car-types")
    public ApiResponse<List<String>> getCarTypes() {
        return new ApiResponse<>(ApiStatus.SUCCESS, CarConstants.CAR_TYPES);
    }

    /**
     * Lấy danh sách tình trạng xe
     *
     * @return List<String>
     */
    @GetMapping("/car-conditions")
    public ApiResponse<List<String>> getCarConditions() {
        return new ApiResponse<>(ApiStatus.SUCCESS, CarConstants.CAR_CONDITIONS);
    }

    /**
     * Lấy danh sách màu sắc xe
     *
     * @return List<String>
     */
    @GetMapping("/car-colors")
    public ApiResponse<List<String>> getCarColors() {
        return new ApiResponse<>(ApiStatus.SUCCESS, CarConstants.CAR_COLORS);
    }

    /**
     * Lấy danh sách trạng thái xe
     *
     * @return List<Map<String, String>>
     */
    @GetMapping("/car-statuses")
    public ApiResponse<List<Map<String, String>>> getCarStatuses() {
        List<Map<String, String>> statuses = Arrays.stream(CarStatus.values())
                .map(status -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("code", status.name());
                    map.put("name", status.getDescription());
                    return map;
                })
                .collect(Collectors.toList());
        return new ApiResponse<>(ApiStatus.SUCCESS, statuses);
    }

    // ========== Excel Import/Export APIs ==========

    /**
     * Tải xuống file Excel mẫu
     * File có 10 dòng trống với dropdown cho các combobox
     *
     * @return File Excel
     */
    @GetMapping("/download-template")
    public ResponseEntity<byte[]> downloadTemplate() {
        ByteArrayOutputStream out = carExcelService.downloadTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "Mau_Danh_Sach_Xe.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(out.toByteArray());
    }

    /**
     * Import dữ liệu từ file Excel
     * Kiểm tra validation, nếu có 1 record lỗi thì throw exception
     *
     * @param file File Excel
     * @return Số lượng record đã import
     */
    @PostMapping(value = "/import-excel", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResponse<Map<String, Object>> importExcel(@RequestParam("file") MultipartFile file) {
        try {
            Integer count = carExcelService.importExcel(file);
            Map<String, Object> response = new HashMap<>();
            response.put("count", count);
            response.put("message", "Import thành công " + count + " xe");
            return new ApiResponse<>(ApiStatus.SUCCESS, response);
        } catch (RuntimeException e) {
            // Handle import errors (duplicate license plate, validation errors, etc.)
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("count", 0);
            errorResponse.put("message", e.getMessage());
            return new ApiResponse<>(ApiStatus.BAD_REQUEST, errorResponse, e.getMessage());
        }
    }

    /**
     * Export báo cáo xe khả dụng cho thuê (PDF)
     */
    @PostMapping("/report/available-cars")
    public ResponseEntity<byte[]> exportAvailableCarsReport(@RequestBody AvailableCarReportRequestDTO request) {
        byte[] pdfBytes = carReportService.exportAvailableCarsReport(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Bao_Cao_Xe_Kha_Dung.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    /**
     * Export báo cáo xe có thể thuê (PDF)
     * Sử dụng cùng logic như màn tạo hợp đồng - chọn xe
     */
    @PostMapping("/report/rentable-cars")
    public ResponseEntity<byte[]> exportRentableCarsReport(@RequestBody RentableCarReportRequestDTO request) {
        byte[] pdfBytes = carReportService.exportRentableCarsReport(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Bao_Cao_Xe_Co_The_Thue.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    /**
     * Export danh sách xe ra file Excel
     * Export các record tìm được theo điều kiện search
     *
     * @param searchDTO Điều kiện tìm kiếm (giống API /list)
     * @return File Excel
     */
    @PostMapping("/export-excel")
    public ResponseEntity<byte[]> exportExcel(@RequestBody CarSearchDTO searchDTO) {
        // Reset page và set size lớn để lấy tất cả dữ liệu
        searchDTO.setPage(1);
        int pageSize = 1000; // Set size để lấy từng batch
        searchDTO.setSize(pageSize);
        
        // Tìm kiếm xe theo điều kiện - lấy trang đầu tiên
        PageableObject<CarDTO> searchResult = carMngService.searchCars(searchDTO);
        
        // Lấy tất cả các xe
        List<CarDTO> allCars = new ArrayList<>(searchResult.getData());
        long totalRecords = searchResult.getTotalRecords();
        
        // Tính số trang cần lấy
        long totalPages = (totalRecords + pageSize - 1) / pageSize; // Làm tròn lên
        
        // Lấy các trang còn lại nếu có
        if (totalPages > 1) {
            for (int page = 2; page <= totalPages; page++) {
                searchDTO.setPage(page);
                PageableObject<CarDTO> pageResult = carMngService.searchCars(searchDTO);
                allCars.addAll(pageResult.getData());
            }
        }
        
        // Convert CarDTO sang CarSaveDTO để export
        List<CarSaveDTO> cars = allCars.stream()
                .map(carDTO -> {
                    CarSaveDTO saveDTO = new CarSaveDTO();
                    saveDTO.setId(carDTO.getId());
                    saveDTO.setModel(carDTO.getModel());
                    saveDTO.setLicensePlate(carDTO.getLicensePlate());
                    saveDTO.setVehicleCode(carDTO.getVehicleCode());
                    saveDTO.setCarType(carDTO.getCarType());
                    saveDTO.setBranchId(carDTO.getBranchId());
                    saveDTO.setBrandId(carDTO.getBrandId());
                    saveDTO.setDailyPrice(carDTO.getDailyPrice());
                    saveDTO.setHourlyPrice(carDTO.getHourlyPrice());
                    saveDTO.setCondition(carDTO.getCondition());
                    saveDTO.setCurrentOdometer(carDTO.getCurrentOdometer());
                    saveDTO.setStatus(carDTO.getStatus());
                    saveDTO.setImageUrl(carDTO.getImageUrl());
                    saveDTO.setNote(carDTO.getNote());
                    saveDTO.setYearOfManufacture(carDTO.getYearOfManufacture());
                    saveDTO.setOrigin(carDTO.getOrigin());
                    saveDTO.setValue(carDTO.getValue());
                    saveDTO.setFrameNumber(carDTO.getFrameNumber());
                    saveDTO.setEngineNumber(carDTO.getEngineNumber());
                    saveDTO.setColor(carDTO.getColor());
                    saveDTO.setRegistrationNumber(carDTO.getRegistrationNumber());
                    saveDTO.setRegisteredOwnerName(carDTO.getRegisteredOwnerName());
                    saveDTO.setRegistrationPlace(carDTO.getRegistrationPlace());
                    saveDTO.setInsuranceContractNumber(carDTO.getInsuranceContractNumber());
                    saveDTO.setInsuranceExpiryDate(carDTO.getInsuranceExpiryDate());
                    return saveDTO;
                })
                .collect(Collectors.toList());
        
        ByteArrayOutputStream out = carExcelService.exportExcel(cars);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "Danh_Sach_Xe.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(out.toByteArray());
    }
}

