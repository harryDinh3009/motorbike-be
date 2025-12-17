package com.motorbikebe.dto.business.admin.brandMng;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BrandSaveDTO {

    /** ID hãng xe (null khi tạo mới) */
    private String id;

    /** Tên hãng xe */
    @NotBlank(message = "Tên hãng xe không được để trống")
    private String name;

    /** Mô tả */
    private String description;
}

