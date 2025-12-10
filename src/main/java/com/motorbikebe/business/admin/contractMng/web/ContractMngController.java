package com.motorbikebe.business.admin.contractMng.web;

import com.motorbikebe.business.admin.contractMng.excel.ContractExcelService;
import com.motorbikebe.business.admin.contractMng.service.ContractMngService;
import com.motorbikebe.business.admin.contractMng.service.ContractReceiptService;
import com.motorbikebe.business.admin.contractMng.service.ContractReportService;
import com.motorbikebe.common.ApiResponse;
import com.motorbikebe.common.ApiStatus;
import com.motorbikebe.common.PageableObject;
import com.motorbikebe.constant.enumconstant.ContractStatus;
import com.motorbikebe.dto.business.admin.contractMng.*;
import com.motorbikebe.entity.domain.ContractEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller quản lý hợp đồng (đã nâng cấp hoàn toàn)
 */
@RestController
@RequestMapping("/a/contract-mng")
@RequiredArgsConstructor
public class ContractMngController {

    private final ContractMngService contractMngService;
    private final ContractExcelService contractExcelService;
    private final ContractReceiptService contractReceiptService;
    private final ContractReportService contractReportService;

    // ========== CRUD Operations ==========

    /**
     * Tìm kiếm hợp đồng với phân trang
     */
    @PostMapping("/list")
    public ApiResponse<PageableObject<ContractDTO>> searchContracts(@RequestBody ContractSearchDTO searchDTO) {
        PageableObject<ContractDTO> pageableRes = contractMngService.searchContracts(searchDTO);
        return new ApiResponse<>(ApiStatus.SUCCESS, pageableRes);
    }

    /**
     * Lấy chi tiết hợp đồng
     */
    @GetMapping("/detail/{id}")
    public ApiResponse<ContractDTO> getContractDetail(@PathVariable String id) {
        ContractDTO response = contractMngService.getContractDetail(id);
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }

    /**
     * Tạo mới hoặc cập nhật hợp đồng
     */
    @PostMapping("/save")
    public ApiResponse<ContractEntity> saveContract(@RequestBody ContractSaveDTO saveDTO) {
        ContractEntity response = contractMngService.saveContract(saveDTO);
        return new ApiResponse<>(ApiStatus.CREATED, response);
    }

    /**
     * Hủy hợp đồng (chuyển status thành CANCELLED)
     */
    @PutMapping("/cancel/{id}")
    public ApiResponse<Boolean> cancelContract(@PathVariable String id) {
        Boolean response = contractMngService.cancelContract(id);
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }

    // ========== Contract Cars ==========

    /**
     * Lấy danh sách xe trong hợp đồng
     */
    @GetMapping("/cars/{contractId}")
    public ApiResponse<List<ContractCarDTO>> getContractCars(@PathVariable String contractId) {
        List<ContractCarDTO> response = contractMngService.getContractCars(contractId);
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }

    /**
     * Thêm xe vào hợp đồng
     */
    @PostMapping("/cars")
    public ApiResponse<ContractCarDTO> addContractCar(@Valid @RequestBody ContractCarCreateDTO createDTO) {
        ContractCarDTO response = contractMngService.addContractCar(createDTO);
        return new ApiResponse<>(ApiStatus.CREATED, response);
    }

    /**
     * Cập nhật xe trong hợp đồng
     */
    @PutMapping("/cars/{id}")
    public ApiResponse<ContractCarDTO> updateContractCar(@PathVariable String id,
                                                         @Valid @RequestBody ContractCarUpdateDTO updateDTO) {
        ContractCarDTO response = contractMngService.updateContractCar(id, updateDTO);
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }

    /**
     * Xóa xe khỏi hợp đồng
     */
    @DeleteMapping("/cars/{id}")
    public ApiResponse<Boolean> deleteContractCar(@PathVariable String id) {
        Boolean response = contractMngService.deleteContractCar(id);
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }

    // ========== Surcharges Management ==========

    /**
     * Thêm phụ thu cho hợp đồng
     */
    @PostMapping("/surcharge/add")
    public ApiResponse<Boolean> addSurcharge(@RequestBody SurchargeSaveDTO saveDTO) {
        Boolean response = contractMngService.addSurcharge(saveDTO);
        return new ApiResponse<>(ApiStatus.CREATED, response);
    }

    /**
     * Cập nhật phụ thu
     */
    @PutMapping("/surcharge/update/{id}")
    public ApiResponse<Boolean> updateSurcharge(@PathVariable String id,
                                                @RequestBody SurchargeSaveDTO saveDTO) {
        Boolean response = contractMngService.updateSurcharge(id, saveDTO);
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }

    /**
     * Xóa phụ thu
     */
    @DeleteMapping("/surcharge/delete/{id}")
    public ApiResponse<Boolean> deleteSurcharge(@PathVariable String id) {
        Boolean response = contractMngService.deleteSurcharge(id);
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }

    /**
     * Lấy danh sách phụ thu theo hợp đồng
     */
    @GetMapping("/surcharge/list/{contractId}")
    public ApiResponse<List<SurchargeDTO>> getSurchargesByContractId(@PathVariable String contractId) {
        List<SurchargeDTO> response = contractMngService.getSurchargesByContractId(contractId);
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }

    // ========== Payment Management ==========

    /**
     * Thêm thanh toán cho hợp đồng
     */
    @PostMapping("/payment/add")
    public ApiResponse<Boolean> addPayment(@RequestBody PaymentTransactionSaveDTO saveDTO) {
        Boolean response = contractMngService.addPayment(saveDTO);
        return new ApiResponse<>(ApiStatus.CREATED, response);
    }

    /**
     * Xóa thanh toán
     */
    @DeleteMapping("/payment/delete/{id}")
    public ApiResponse<Boolean> deletePayment(@PathVariable String id) {
        Boolean response = contractMngService.deletePayment(id);
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }

    /**
     * Lấy lịch sử thanh toán
     */
    @GetMapping("/payment/history/{contractId}")
    public ApiResponse<List<PaymentTransactionDTO>> getPaymentHistory(@PathVariable String contractId) {
        List<PaymentTransactionDTO> response = contractMngService.getPaymentHistory(contractId);
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }

    // ========== Delivery Process ==========

    /**
     * Cập nhật thông tin giao xe
     */
    @PostMapping("/delivery/update")
    public ApiResponse<Boolean> updateDelivery(@RequestBody ContractDeliveryDTO deliveryDTO) {
        Boolean response = contractMngService.updateDelivery(deliveryDTO);
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }

    /**
     * Upload ảnh giao xe (nhiều ảnh)
     */
    @PostMapping(value = "/delivery/upload-images/{contractId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResponse<Map<String, Object>> uploadDeliveryImages(
            @PathVariable String contractId,
            @RequestParam("files") List<MultipartFile> files) {
        
        List<String> imageUrls = contractMngService.uploadDeliveryImages(contractId, files);
        
        Map<String, Object> response = new HashMap<>();
        response.put("count", imageUrls.size());
        response.put("imageUrls", imageUrls);
        response.put("message", "Upload thành công " + imageUrls.size() + " ảnh");
        
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }

    // ========== Return Process ==========

    /**
     * Cập nhật thông tin nhận xe
     */
    @PostMapping("/return/update")
    public ApiResponse<Boolean> updateReturn(@RequestBody ContractReturnDTO returnDTO) {
        Boolean response = contractMngService.updateReturn(returnDTO);
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }

    /**
     * Upload ảnh nhận xe (nhiều ảnh)
     */
    @PostMapping(value = "/return/upload-images/{contractId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResponse<Map<String, Object>> uploadReturnImages(
            @PathVariable String contractId,
            @RequestParam("files") List<MultipartFile> files) {
        
        List<String> imageUrls = contractMngService.uploadReturnImages(contractId, files);
        
        Map<String, Object> response = new HashMap<>();
        response.put("count", imageUrls.size());
        response.put("imageUrls", imageUrls);
        response.put("message", "Upload thành công " + imageUrls.size() + " ảnh");
        
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }

    // ========== Complete Contract ==========

    /**
     * Đóng hợp đồng (hoàn thành thanh toán)
     */
    @PostMapping("/complete")
    public ApiResponse<Boolean> completeContract(@RequestBody ContractCompleteDTO completeDTO) {
        Boolean response = contractMngService.completeContract(completeDTO);
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }

    // ========== PDF Generation ==========

    /**
     * Tải xuống file PDF hợp đồng
     */
    @GetMapping("/download-pdf/{id}")
    public ResponseEntity<byte[]> downloadContractPDF(@PathVariable String id) {
        try {
            byte[] pdfBytes = contractMngService.downloadContractPDF(id);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    "hop-dong-thue-xe-" + id + ".pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== Excel Export ==========

    /**
     * Xuất danh sách hợp đồng ra Excel theo điều kiện tìm kiếm
     */
    @PostMapping("/export-excel")
    public ResponseEntity<byte[]> exportContractsToExcel(@RequestBody ContractSearchDTO searchDTO) {
        ByteArrayOutputStream out = contractExcelService.exportContracts(searchDTO);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "Danh_Sach_Hop_Dong.xlsx");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(out.toByteArray());
    }

    /**
     * Lấy danh sách trạng thái hợp đồng
     *
     * @return List<Map<String, String>>
     */
    @GetMapping("/contract-statuses")
    public ApiResponse<List<Map<String, String>>> getContractStatuses() {
        List<Map<String, String>> statuses = Arrays.stream(ContractStatus.values())
                .map(status -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("code", status.name());
                    map.put("name", status.getDescription());
                    return map;
                })
                .collect(Collectors.toList());
        return new ApiResponse<>(ApiStatus.SUCCESS, statuses);
    }

    /**
     * Export biên nhận trả xe
     */
    @PostMapping("/receipt/export")
    public ResponseEntity<byte[]> exportContractReceipt(@RequestBody ContractReceiptRequestDTO requestDTO) {
        byte[] pdfBytes = contractReceiptService.exportReceipt(requestDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment",
                "bien-nhan-hop-dong-" + (requestDTO.getContractId() != null ? requestDTO.getContractId() : "") + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    /**
     * Lấy dữ liệu báo cáo doanh thu theo tháng
     */
    @PostMapping("/revenue/monthly-data")
    public ApiResponse<List<MonthlyRevenueRowDTO>> getMonthlyRevenueData(@RequestBody MonthlyRevenueReportRequestDTO requestDTO) {
        List<MonthlyRevenueRowDTO> data = contractReportService.getMonthlyRevenueData(requestDTO);
        return new ApiResponse<>(ApiStatus.SUCCESS, data);
    }

    /**
     * Export báo cáo doanh thu theo tháng (PDF)
     */
    @PostMapping("/revenue/monthly-report")
    public ResponseEntity<byte[]> exportMonthlyRevenueReport(@RequestBody MonthlyRevenueReportRequestDTO requestDTO) {
        byte[] pdfBytes = contractReportService.exportMonthlyRevenueReport(requestDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment",
                "Bao_Cao_Doanh_Thu_Thang_" + requestDTO.getYear() + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    /**
     * Lấy dữ liệu báo cáo doanh thu theo ngày
     */
    @PostMapping("/revenue/daily-data")
    public ApiResponse<List<DailyRevenueRowDTO>> getDailyRevenueData(@RequestBody DailyRevenueReportRequestDTO requestDTO) {
        List<DailyRevenueRowDTO> data = contractReportService.getDailyRevenueData(requestDTO);
        return new ApiResponse<>(ApiStatus.SUCCESS, data);
    }

    /**
     * Export báo cáo doanh thu theo ngày (PDF)
     */
    @PostMapping("/revenue/daily-report")
    public ResponseEntity<byte[]> exportDailyRevenueReport(@RequestBody DailyRevenueReportRequestDTO requestDTO) {
        byte[] pdfBytes = contractReportService.exportDailyRevenueReport(requestDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment",
                "Bao_Cao_Doanh_Thu_Theo_Ngay.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    /**
     * Lấy dữ liệu thống kê lượt thuê theo mẫu xe
     */
    @PostMapping("/rental/model-data")
    public ApiResponse<List<ModelRentalRowDTO>> getModelRentalData(@RequestBody ModelRentalReportRequestDTO requestDTO) {
        List<ModelRentalRowDTO> data = contractReportService.getModelRentalData(requestDTO);
        return new ApiResponse<>(ApiStatus.SUCCESS, data);
    }

    /**
     * Export thống kê lượt thuê theo mẫu xe (PDF)
     */
    @PostMapping("/rental/model-report")
    public ResponseEntity<byte[]> exportModelRentalReport(@RequestBody ModelRentalReportRequestDTO requestDTO) {
        byte[] pdfBytes = contractReportService.exportModelRentalReport(requestDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment",
                "Thong_Ke_Luot_Thue_Theo_Mau_Xe.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    // ========== Contract Schedule ==========

    /**
     * Lấy dữ liệu lịch đặt xe
     * Trả về danh sách contract_car trong khoảng thời gian với filter theo branch và status
     */
    @PostMapping("/schedule")
    public ApiResponse<List<ContractScheduleItemDTO>> getContractSchedule(@RequestBody ContractScheduleRequestDTO requestDTO) {
        List<ContractScheduleItemDTO> data = contractMngService.getContractSchedule(requestDTO);
        return new ApiResponse<>(ApiStatus.SUCCESS, data);
    }

    // ========== Car Availability Check ==========

    /**
     * Check availability của nhiều xe cùng lúc
     * Trả về Map<carId, isAvailable>
     */
    @PostMapping("/check-cars-availability")
    public ApiResponse<Map<String, Boolean>> checkCarsAvailability(@RequestBody CheckCarsAvailabilityDTO requestDTO) {
        Map<String, Boolean> response = contractMngService.checkCarsAvailability(requestDTO);
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }
}
