package com.motorbikebe.business.admin.contractMng.excel;

import com.motorbikebe.dto.business.admin.contractMng.ContractSearchDTO;

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

