package com.motorbikebe.dto.business.admin.dataConfiguration;

import com.motorbikebe.dto.common.PageableDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataConfigurationSearchDTO extends PageableDTO {
    String name;

    Integer status;
}
