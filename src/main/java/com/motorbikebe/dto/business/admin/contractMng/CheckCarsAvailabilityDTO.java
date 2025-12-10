package com.motorbikebe.dto.business.admin.contractMng;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO để check availability của nhiều xe cùng lúc
 */
@Getter
@Setter
public class CheckCarsAvailabilityDTO {

    /** Danh sách ID xe cần check */
    private List<String> carIds;

    /** Ngày bắt đầu thuê (ISO format: yyyy-MM-dd'T'HH:mm:ss hoặc yyyy-MM-dd'T'HH:mm) */
    private String startDate;

    /** Ngày kết thúc thuê (ISO format: yyyy-MM-dd'T'HH:mm:ss hoặc yyyy-MM-dd'T'HH:mm) */
    private String endDate;

    /** ID hợp đồng cần loại trừ (khi đang edit hợp đồng) */
    private String excludeContractId;
}

