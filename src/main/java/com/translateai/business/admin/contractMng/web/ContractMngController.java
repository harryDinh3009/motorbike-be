package com.translateai.business.admin.contractMng.web;

import com.translateai.business.admin.contractMng.service.ContractMngService;
import com.translateai.common.ApiResponse;
import com.translateai.common.ApiStatus;
import com.translateai.common.PageableObject;
import com.translateai.dto.business.admin.contractMng.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/a/contract-mng")
@RequiredArgsConstructor
public class ContractMngController {

    private final ContractMngService contractMngService;

    /**
     * Tìm kiếm hợp đồng với phân trang
     *
     * @param searchDTO DTO tìm kiếm
     * @return PageableObject<ContractDTO>
     */
    @GetMapping("/list")
    public ApiResponse<PageableObject<ContractDTO>> searchContracts(ContractSearchDTO searchDTO) {
        PageableObject<ContractDTO> pageableRes = contractMngService.searchContracts(searchDTO);
        return new ApiResponse<>(ApiStatus.SUCCESS, pageableRes);
    }

    /**
     * Lấy chi tiết hợp đồng
     *
     * @param id ID hợp đồng
     * @return ContractDTO
     */
    @GetMapping("/detail")
    public ApiResponse<ContractDTO> getContractDetail(@RequestParam("id") String id) {
        ContractDTO response = contractMngService.getContractDetail(id);
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }

    /**
     * Tạo mới hoặc cập nhật hợp đồng
     *
     * @param saveDTO DTO lưu hợp đồng
     * @return Boolean
     */
    @PostMapping("/save")
    public ApiResponse<Boolean> saveContract(@RequestBody ContractSaveDTO saveDTO) {
        Boolean response = contractMngService.saveContract(saveDTO);
        return new ApiResponse<>(ApiStatus.CREATED, response);
    }

    /**
     * Cập nhật trạng thái hợp đồng (Giao xe, Nhận xe)
     *
     * @param updateStatusDTO DTO cập nhật trạng thái
     * @return Boolean
     */
    @PostMapping("/update-status")
    public ApiResponse<Boolean> updateContractStatus(@RequestBody ContractUpdateStatusDTO updateStatusDTO) {
        Boolean response = contractMngService.updateContractStatus(updateStatusDTO);
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }

    /**
     * Xóa hợp đồng
     *
     * @param id ID hợp đồng
     * @return Boolean
     */
    @DeleteMapping("/delete")
    public ApiResponse<Boolean> deleteContract(@RequestParam("id") String id) {
        Boolean response = contractMngService.deleteContract(id);
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }

    /**
     * Sinh file PDF hợp đồng
     *
     * @param id ID hợp đồng
     * @return String URL của file PDF
     */
    @PostMapping("/generate-pdf")
    public ApiResponse<String> generateContractPDF(@RequestParam("id") String id) {
        String response = contractMngService.generateContractPDF(id);
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }

    /**
     * Tải file PDF hợp đồng
     *
     * @param id ID hợp đồng
     * @return File PDF
     */
    @GetMapping("/download-pdf")
    public ResponseEntity<Resource> downloadContractPDF(@RequestParam("id") String id) {
        ContractDTO contract = contractMngService.getContractDetail(id);
        
        if (contract.getContractFileUrl() == null) {
            return ResponseEntity.notFound().build();
        }
        
        File file = new File(contract.getContractFileUrl());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        
        Resource resource = new FileSystemResource(file);
        
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(resource);
    }

    /**
     * Thêm phụ phí cho hợp đồng
     *
     * @param saveDTO DTO lưu phụ phí
     * @return Boolean
     */
    @PostMapping("/surcharge/add")
    public ApiResponse<Boolean> addSurcharge(@RequestBody SurchargeSaveDTO saveDTO) {
        Boolean response = contractMngService.addSurcharge(saveDTO);
        return new ApiResponse<>(ApiStatus.CREATED, response);
    }

    /**
     * Xóa phụ phí
     *
     * @param id ID phụ phí
     * @return Boolean
     */
    @DeleteMapping("/surcharge/delete")
    public ApiResponse<Boolean> deleteSurcharge(@RequestParam("id") String id) {
        Boolean response = contractMngService.deleteSurcharge(id);
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }

    /**
     * Lấy danh sách phụ phí theo hợp đồng
     *
     * @param contractId ID hợp đồng
     * @return List<SurchargeDTO>
     */
    @GetMapping("/surcharge/list")
    public ApiResponse<List<SurchargeDTO>> getSurchargesByContractId(@RequestParam("contractId") String contractId) {
        List<SurchargeDTO> response = contractMngService.getSurchargesByContractId(contractId);
        return new ApiResponse<>(ApiStatus.SUCCESS, response);
    }
}

