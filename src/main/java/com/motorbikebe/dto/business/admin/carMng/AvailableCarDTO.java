package com.motorbikebe.dto.business.admin.carMng;

import com.motorbikebe.constant.enumconstant.CarStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO lightweight cho màn chọn xe khả dụng
 * Chỉ chứa các field cần thiết để giảm dữ liệu truyền tải
 */
@Getter
@Setter
@NoArgsConstructor
public class AvailableCarDTO {

    /** ID xe */
    private String id;

    /** Mẫu xe */
    private String model;

    /** Biển số xe */
    private String licensePlate;

    /** Mã xe */
    private String vehicleCode;

    /** Loại xe */
    private String carType;

    /** ID chi nhánh */
    private String branchId;

    /** Tên chi nhánh */
    private String branchName;

    /** Giá thuê theo ngày */
    private BigDecimal dailyPrice;

    /** Giá thuê theo giờ */
    private BigDecimal hourlyPrice;

    /** Trạng thái */
    private CarStatus status;

    /** Tên trạng thái */
    private String statusNm;

    /** URL ảnh xe */
    private String imageUrl;

    /**
     * Constructor cho Spring Data JPA native query projection (10 parameters)
     * Note: status nhận String từ database và convert sang enum
     */
    public AvailableCarDTO(String id, String model, String licensePlate,
                          String carType, String branchId, String branchName,
                          BigDecimal dailyPrice, BigDecimal hourlyPrice,
                          String status, String imageUrl) {
        this.id = id;
        this.model = model;
        this.licensePlate = licensePlate;
        this.carType = carType;
        this.branchId = branchId;
        this.branchName = branchName;
        this.dailyPrice = dailyPrice;
        this.hourlyPrice = hourlyPrice;
        // Convert String to CarStatus enum
        this.status = (status != null) ? CarStatus.valueOf(status) : null;
        this.imageUrl = imageUrl;
    }
}

