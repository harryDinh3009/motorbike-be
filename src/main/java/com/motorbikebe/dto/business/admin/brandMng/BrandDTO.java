package com.motorbikebe.dto.business.admin.brandMng;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BrandDTO {

    /** ID hãng xe */
    private String id;

    /** Tên hãng xe */
    private String name;

    /** Mô tả */
    private String description;
}

