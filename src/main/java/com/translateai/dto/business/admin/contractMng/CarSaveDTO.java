package com.translateai.dto.business.admin.contractMng;

import com.translateai.constant.enumconstant.CarStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CarSaveDTO {

    /** ID xe (null khi tạo mới) */
    private String id;

    /** Tên xe */
    private String name;

    /** Biển số xe */
    private String licensePlate;

    /** Loại xe */
    private String carType;

    /** Giá thuê theo ngày */
    private BigDecimal dailyPrice;

    /** Trạng thái xe */
    private CarStatus status;

    /** URL hình ảnh xe */
    private String imageUrl;

    /** Mô tả xe */
    private String description;
}

