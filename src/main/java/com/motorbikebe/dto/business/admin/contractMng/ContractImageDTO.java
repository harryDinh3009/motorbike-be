package com.motorbikebe.dto.business.admin.contractMng;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO hiển thị ảnh xe trong hợp đồng
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContractImageDTO {
    private String id;
    private String contractId;
    private String imageType; // DELIVERY / RETURN
    private String imageUrl;
    private Integer displayOrder;
    private String notes;
}

