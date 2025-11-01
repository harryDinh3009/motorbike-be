package com.translateai.business.admin.contractMng.excel;

import com.translateai.dto.business.admin.contractMng.ContractSearchDTO;

import java.io.ByteArrayOutputStream;

/**
 * Service interface for Contract Excel operations
 */
public interface ContractExcelService {
    
    /**
     * Export contracts to Excel based on search criteria
     * 
     * @param searchDTO Search criteria
     * @return ByteArrayOutputStream containing Excel file
     */
    ByteArrayOutputStream exportContracts(ContractSearchDTO searchDTO);
}

