package com.motorbikebe.dto.business.admin.surchargeTypeMng;

import com.motorbikebe.dto.common.PageableDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SurchargeTypeSearchDTO extends PageableDTO {
    private String keyword; // Tìm theo tên phụ thu
    private Integer status; // Lọc theo trạng thái
}

