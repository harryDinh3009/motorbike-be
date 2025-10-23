package com.translateai.business.admin.contractMng.service;

import com.translateai.common.PageableObject;
import com.translateai.dto.business.admin.contractMng.*;
import jakarta.validation.Valid;

import java.util.List;

public interface ContractMngService {

    /**
     * Tìm kiếm hợp đồng với phân trang
     *
     * @param searchDTO DTO tìm kiếm
     * @return PageableObject<ContractDTO>
     */
    PageableObject<ContractDTO> searchContracts(ContractSearchDTO searchDTO);

    /**
     * Lấy chi tiết hợp đồng
     *
     * @param id ID hợp đồng
     * @return ContractDTO
     */
    ContractDTO getContractDetail(String id);

    /**
     * Tạo mới hoặc cập nhật hợp đồng
     *
     * @param saveDTO DTO lưu hợp đồng
     * @return Boolean
     */
    Boolean saveContract(@Valid ContractSaveDTO saveDTO);

    /**
     * Cập nhật trạng thái hợp đồng (Giao xe, Nhận xe)
     *
     * @param updateStatusDTO DTO cập nhật trạng thái
     * @return Boolean
     */
    Boolean updateContractStatus(@Valid ContractUpdateStatusDTO updateStatusDTO);

    /**
     * Xóa hợp đồng
     *
     * @param id ID hợp đồng
     * @return Boolean
     */
    Boolean deleteContract(String id);

    /**
     * Sinh file PDF hợp đồng
     *
     * @param id ID hợp đồng
     * @return String URL của file PDF
     */
    String generateContractPDF(String id);

    /**
     * Thêm phụ phí cho hợp đồng
     *
     * @param saveDTO DTO lưu phụ phí
     * @return Boolean
     */
    Boolean addSurcharge(@Valid SurchargeSaveDTO saveDTO);

    /**
     * Xóa phụ phí
     *
     * @param id ID phụ phí
     * @return Boolean
     */
    Boolean deleteSurcharge(String id);

    /**
     * Lấy danh sách phụ phí theo hợp đồng
     *
     * @param contractId ID hợp đồng
     * @return List<SurchargeDTO>
     */
    List<SurchargeDTO> getSurchargesByContractId(String contractId);
}

