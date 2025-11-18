package com.motorbikebe.business.admin.contractMng.service;

import com.motorbikebe.dto.business.admin.contractMng.ContractReceiptRequestDTO;

public interface ContractReceiptService {
    byte[] exportReceipt(ContractReceiptRequestDTO request);
}

