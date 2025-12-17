package com.motorbikebe.repository.business.admin;

import com.motorbikebe.constant.enumconstant.CarStatus;
import com.motorbikebe.dto.business.admin.carMng.AvailableCarDTO;
import com.motorbikebe.dto.business.admin.carMng.CarDTO;
import com.motorbikebe.dto.business.admin.carMng.CarSearchAvailableDTO;
import com.motorbikebe.dto.business.admin.carMng.CarSearchDTO;
import com.motorbikebe.dto.business.admin.carMng.ConflictingContractDTO;
import com.motorbikebe.entity.domain.CarEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<CarEntity, String> {

    @Query(value = """
            SELECT c.id,
                   c.model,
                   c.license_plate AS licensePlate,
                   c.car_type AS carType,
                   c.branch_id AS branchId,
                   b.name AS branchName,
                   c.brand_id AS brandId,
                   br.name AS brandName,
                   c.daily_price AS dailyPrice,
                   c.hourly_price AS hourlyPrice,
                   c.condition,
                   c.current_odometer AS currentOdometer,
                   c.status,
                   c.image_url AS imageUrl,
                   c.note,
                   c.year_of_manufacture AS yearOfManufacture,
                   c.origin,
                   c.value,
                   c.frame_number AS frameNumber,
                   c.engine_number AS engineNumber,
                   c.color,
                   c.registration_number AS registrationNumber,
                   c.registered_owner_name AS registeredOwnerName,
                   c.registration_place AS registrationPlace,
                   c.insurance_contract_number AS insuranceContractNumber,
                   c.insurance_expiry_date AS insuranceExpiryDate
            FROM car c
            LEFT JOIN branch b ON c.branch_id = b.id
            LEFT JOIN brand br ON c.brand_id = br.id
            WHERE (:#{#req.keyword} IS NULL OR :#{#req.keyword} = '' 
                   OR c.model LIKE %:#{#req.keyword}% 
                   OR c.license_plate LIKE %:#{#req.keyword}%)
            AND (:#{#req.branchId} IS NULL OR :#{#req.branchId} = '' OR c.branch_id = :#{#req.branchId})
            AND (:#{#req.modelName} IS NULL OR :#{#req.modelName} = '' OR c.model = :#{#req.modelName})
            AND (:#{#req.carType} IS NULL OR :#{#req.carType} = '' OR c.car_type = :#{#req.carType})
            AND (:#{#req.condition} IS NULL OR :#{#req.condition} = '' OR c.condition = :#{#req.condition})
            AND (:#{#req.status?.name()} IS NULL OR c.status = :#{#req.status?.name()})
            ORDER BY c.created_date DESC
            """, countQuery = """
            SELECT COUNT(c.id)
            FROM car c
            LEFT JOIN branch b ON c.branch_id = b.id
            LEFT JOIN brand br ON c.brand_id = br.id
            WHERE (:#{#req.keyword} IS NULL OR :#{#req.keyword} = '' 
                   OR c.model LIKE %:#{#req.keyword}% 
                   OR c.license_plate LIKE %:#{#req.keyword}%)
            AND (:#{#req.branchId} IS NULL OR :#{#req.branchId} = '' OR c.branch_id = :#{#req.branchId})
            AND (:#{#req.modelName} IS NULL OR :#{#req.modelName} = '' OR c.model = :#{#req.modelName})
            AND (:#{#req.carType} IS NULL OR :#{#req.carType} = '' OR c.car_type = :#{#req.carType})
            AND (:#{#req.condition} IS NULL OR :#{#req.condition} = '' OR c.condition = :#{#req.condition})
            AND (:#{#req.status?.name()} IS NULL OR c.status = :#{#req.status?.name()})
            """, nativeQuery = true)
    Page<CarDTO> searchCars(Pageable pageable, @Param("req") CarSearchDTO req);

    CarEntity findByLicensePlate(String licensePlate);

    List<CarEntity> findByStatus(CarStatus status);
    
    List<CarEntity> findByBranchId(String branchId);

    /**
     * Kiểm tra có xe nào dùng model này không
     */
    @Query(value = """
            SELECT COUNT(c.id)
            FROM car c
            WHERE c.model = :modelName
            """, nativeQuery = true)
    Long existsByModel(@Param("modelName") String modelName);

    /**
     * Kiểm tra có xe nào dùng brand này không
     */
    @Query(value = """
            SELECT COUNT(c.id)
            FROM car c
            WHERE c.brand_id = :brandId
            """, nativeQuery = true)
    Long existsByBrandId(@Param("brandId") String brandId);

    @Query(value = """
            SELECT COUNT(c.id)
            FROM car c
            WHERE (:branchId IS NULL OR :branchId = '' OR c.branch_id = :branchId)
              AND c.status <> 'LOST'
            """, nativeQuery = true)
    long countActiveCarsByBranch(@Param("branchId") String branchId);

    @Query(value = """
            SELECT c.id,
                   c.model,
                   c.license_plate AS licensePlate,
                   c.car_type AS carType,
                   c.branch_id AS branchId,
                   b.name AS branchName,
                   c.brand_id AS brandId,
                   br.name AS brandName,
                   c.daily_price AS dailyPrice,
                   c.hourly_price AS hourlyPrice,
                   c.condition,
                   c.current_odometer AS currentOdometer,
                   CASE
                       WHEN :#{#req.startDate} IS NOT NULL 
                            AND :#{#req.endDate} IS NOT NULL
                            AND EXISTS (
                                SELECT 1 
                                FROM contract_car cc
                                INNER JOIN contract con ON cc.contract_id = con.id
                                WHERE cc.car_id = c.id
                                AND con.status IN ('CONFIRMED', 'DELIVERED')
                                AND (
                                    (:#{#req.startDate} BETWEEN con.start_date AND con.end_date)
                                    OR (:#{#req.endDate} BETWEEN con.start_date AND con.end_date)
                                    OR (con.start_date BETWEEN :#{#req.startDate} AND :#{#req.endDate})
                                    OR (con.end_date BETWEEN :#{#req.startDate} AND :#{#req.endDate})
                                )
                            )
                       THEN 'NOT_AVAILABLE'
                       ELSE c.status
                   END AS status,
                   c.image_url AS imageUrl,
                   c.note,
                   c.year_of_manufacture AS yearOfManufacture,
                   c.origin,
                   c.value,
                   c.frame_number AS frameNumber,
                   c.engine_number AS engineNumber,
                   c.color,
                   c.registration_number AS registrationNumber,
                   c.registered_owner_name AS registeredOwnerName,
                   c.registration_place AS registrationPlace,
                   c.insurance_contract_number AS insuranceContractNumber,
                   c.insurance_expiry_date AS insuranceExpiryDate
            FROM car c
            LEFT JOIN branch b ON c.branch_id = b.id
            LEFT JOIN brand br ON c.brand_id = br.id
            WHERE (:#{#req.keyword} IS NULL OR :#{#req.keyword} = '' 
                   OR c.model LIKE %:#{#req.keyword}% 
                   OR c.license_plate LIKE %:#{#req.keyword}%)
            AND (:#{#req.branchId} IS NULL OR :#{#req.branchId} = '' OR c.branch_id = :#{#req.branchId})
            AND (:#{#req.modelName} IS NULL OR :#{#req.modelName} = '' OR c.model = :#{#req.modelName})
            AND (:#{#req.carType} IS NULL OR :#{#req.carType} = '' OR c.car_type = :#{#req.carType})
            AND (:#{#req.condition} IS NULL OR :#{#req.condition} = '' OR c.condition = :#{#req.condition})
            AND c.status = 'AVAILABLE'
            ORDER BY c.created_date DESC
            """, countQuery = """
            SELECT COUNT(c.id)
            FROM car c
            LEFT JOIN branch b ON c.branch_id = b.id
            LEFT JOIN brand br ON c.brand_id = br.id
            WHERE (:#{#req.keyword} IS NULL OR :#{#req.keyword} = '' 
                   OR c.model LIKE %:#{#req.keyword}% 
                   OR c.license_plate LIKE %:#{#req.keyword}%)
            AND (:#{#req.branchId} IS NULL OR :#{#req.branchId} = '' OR c.branch_id = :#{#req.branchId})
            AND (:#{#req.modelName} IS NULL OR :#{#req.modelName} = '' OR c.model = :#{#req.modelName})
            AND (:#{#req.carType} IS NULL OR :#{#req.carType} = '' OR c.car_type = :#{#req.carType})
            AND (:#{#req.condition} IS NULL OR :#{#req.condition} = '' OR c.condition = :#{#req.condition})
            AND c.status = 'AVAILABLE'
            """, nativeQuery = true)
    Page<CarDTO> searchAvailableCars(Pageable pageable, @Param("req") CarSearchDTO req);

    /**
     * Tìm kiếm xe khả dụng (lightweight) - chỉ trả về các field cần thiết
     * Dùng cho màn chọn xe khi tạo hợp đồng
     */
    @Query(value = """
            SELECT c.id,
                   c.model,
                   c.license_plate AS licensePlate,
                   c.car_type AS carType,
                   c.branch_id AS branchId,
                   b.name AS branchName,
                   c.daily_price AS dailyPrice,
                   c.hourly_price AS hourlyPrice,
                   CASE
                       WHEN :#{#req.startDate} IS NOT NULL 
                            AND :#{#req.endDate} IS NOT NULL
                            AND EXISTS (
                                SELECT 1 
                                FROM contract_car cc
                                INNER JOIN contract con ON cc.contract_id = con.id
                                WHERE cc.car_id = c.id
                                AND con.status IN ('CONFIRMED', 'DELIVERED')
                                AND (
                                    (:#{#req.startDate} BETWEEN con.start_date AND con.end_date)
                                    OR (:#{#req.endDate} BETWEEN con.start_date AND con.end_date)
                                    OR (con.start_date BETWEEN :#{#req.startDate} AND :#{#req.endDate})
                                    OR (con.end_date BETWEEN :#{#req.startDate} AND :#{#req.endDate})
                                )
                            )
                       THEN 'NOT_AVAILABLE'
                       ELSE c.status
                   END AS status,
                   c.image_url AS imageUrl
            FROM car c
            LEFT JOIN branch b ON c.branch_id = b.id
            WHERE (:#{#req.keyword} IS NULL OR :#{#req.keyword} = '' 
                   OR c.model LIKE %:#{#req.keyword}% 
                   OR c.license_plate LIKE %:#{#req.keyword}%)
            AND (:#{#req.branchId} IS NULL OR :#{#req.branchId} = '' OR c.branch_id = :#{#req.branchId})
            AND (:#{#req.modelName} IS NULL OR :#{#req.modelName} = '' OR c.model = :#{#req.modelName})
            AND (:#{#req.carType} IS NULL OR :#{#req.carType} = '' OR c.car_type = :#{#req.carType})
            AND (:#{#req.condition} IS NULL OR :#{#req.condition} = '' OR c.condition = :#{#req.condition})
            ORDER BY c.model ASC, c.license_plate ASC
            """, countQuery = """
            SELECT COUNT(c.id)
            FROM car c
            LEFT JOIN branch b ON c.branch_id = b.id
            WHERE (:#{#req.keyword} IS NULL OR :#{#req.keyword} = '' 
                   OR c.model LIKE %:#{#req.keyword}% 
                   OR c.license_plate LIKE %:#{#req.keyword}%)
            AND (:#{#req.branchId} IS NULL OR :#{#req.branchId} = '' OR c.branch_id = :#{#req.branchId})
            AND (:#{#req.modelName} IS NULL OR :#{#req.modelName} = '' OR c.model = :#{#req.modelName})
            AND (:#{#req.carType} IS NULL OR :#{#req.carType} = '' OR c.car_type = :#{#req.carType})
            AND (:#{#req.condition} IS NULL OR :#{#req.condition} = '' OR c.condition = :#{#req.condition})
            """, nativeQuery = true)
    Page<AvailableCarDTO> searchAvailableCarsLight(Pageable pageable, @Param("req") CarSearchDTO req);

    @Query(value = """
            SELECT c.id,
                   c.model,
                   c.license_plate AS licensePlate,
                   c.car_type AS carType,
                   c.branch_id AS branchId,
                   b.name AS branchName,
                   c.brand_id AS brandId,
                   br.name AS brandName,
                   c.daily_price AS dailyPrice,
                   c.hourly_price AS hourlyPrice,
                   c.condition,
                   c.current_odometer AS currentOdometer,
                   c.status,
                   c.image_url AS imageUrl,
                   c.note,
                   c.year_of_manufacture AS yearOfManufacture,
                   c.origin,
                   c.value,
                   c.frame_number AS frameNumber,
                   c.engine_number AS engineNumber,
                   c.color,
                   c.registration_number AS registrationNumber,
                   c.registered_owner_name AS registeredOwnerName,
                   c.registration_place AS registrationPlace,
                   c.insurance_contract_number AS insuranceContractNumber,
                   c.insurance_expiry_date AS insuranceExpiryDate
            FROM car c
            LEFT JOIN branch b ON c.branch_id = b.id
            LEFT JOIN brand br ON c.brand_id = br.id
            WHERE (:#{#req.branchId} IS NULL OR :#{#req.branchId} = '' OR c.branch_id = :#{#req.branchId})
              AND (:#{#req.modelName} IS NULL OR :#{#req.modelName} = '' OR c.model = :#{#req.modelName})
              AND (:#{#req.carType} IS NULL OR :#{#req.carType} = '' OR c.car_type = :#{#req.carType})
              AND c.status = 'AVAILABLE'
              AND (
                :#{#req.startDate} IS NULL OR :#{#req.endDate} IS NULL OR NOT EXISTS (
                    SELECT 1
                    FROM contract_car cc
                    INNER JOIN contract con ON cc.contract_id = con.id
                    WHERE cc.car_id = c.id
                      AND con.status IN ('CONFIRMED', 'DELIVERED')
                      AND (
                          (:#{#req.startDate} BETWEEN con.start_date AND con.end_date)
                          OR (:#{#req.endDate} BETWEEN con.start_date AND con.end_date)
                          OR (con.start_date BETWEEN :#{#req.startDate} AND :#{#req.endDate})
                          OR (con.end_date BETWEEN :#{#req.startDate} AND :#{#req.endDate})
                      )
                )
              )
            ORDER BY c.model ASC, c.license_plate ASC
            """, nativeQuery = true)
    List<CarDTO> findAvailableCarsForReport(@Param("req") CarSearchDTO req);

    @Query(value = """
            SELECT c.id,
                   c.model,
                   c.license_plate AS licensePlate,
                   c.car_type AS carType,
                   c.branch_id AS branchId,
                   b.name AS branchName,
                   c.brand_id AS brandId,
                   br.name AS brandName,
                   c.daily_price AS dailyPrice,
                   c.hourly_price AS hourlyPrice,
                   c.condition,
                   c.current_odometer AS currentOdometer,
                   c.status,
                   c.image_url AS imageUrl,
                   c.note,
                   c.year_of_manufacture AS yearOfManufacture,
                   c.origin,
                   c.value,
                   c.frame_number AS frameNumber,
                   c.engine_number AS engineNumber,
                   c.color,
                   c.registration_number AS registrationNumber,
                   c.registered_owner_name AS registeredOwnerName,
                   c.registration_place AS registrationPlace,
                   c.insurance_contract_number AS insuranceContractNumber,
                   c.insurance_expiry_date AS insuranceExpiryDate
            FROM car c
            LEFT JOIN branch b ON c.branch_id = b.id
            LEFT JOIN brand br ON c.brand_id = br.id
            WHERE (:#{#req.branchId} IS NULL OR :#{#req.branchId} = '' OR c.branch_id = :#{#req.branchId})
              AND (:#{#req.modelName} IS NULL OR :#{#req.modelName} = '' OR c.model = :#{#req.modelName})
              AND (:#{#req.carType} IS NULL OR :#{#req.carType} = '' OR c.car_type = :#{#req.carType})
              AND c.status = 'AVAILABLE'
              AND (
                :#{#req.startDate} IS NULL OR :#{#req.endDate} IS NULL OR NOT EXISTS (
                    SELECT 1
                    FROM contract_car cc
                    INNER JOIN contract con ON cc.contract_id = con.id
                    WHERE cc.car_id = c.id
                      AND con.status IN ('CONFIRMED', 'DELIVERED')
                      AND (
                          (:#{#req.startDate} BETWEEN con.start_date AND con.end_date)
                          OR (:#{#req.endDate} BETWEEN con.start_date AND con.end_date)
                          OR (con.start_date BETWEEN :#{#req.startDate} AND :#{#req.endDate})
                          OR (con.end_date BETWEEN :#{#req.startDate} AND :#{#req.endDate})
                      )
                )
              )
            ORDER BY c.model ASC, c.license_plate ASC
            """, nativeQuery = true)
    List<CarDTO> findAvailableCarsForReportAvailable(@Param("req") CarSearchAvailableDTO req);

    /**
     * Lấy danh sách hợp đồng conflict với xe trong khoảng thời gian
     * 
     * @param carId ID xe
     * @param startDate Ngày bắt đầu (ISO string)
     * @param endDate Ngày kết thúc (ISO string)
     * @return List<ConflictingContractDTO>
     */
    @Query(value = """
            SELECT con.id,
                   con.contract_code AS contractCode,
                   cus.full_name AS customerName,
                   con.start_date AS startDate,
                   con.end_date AS endDate,
                   con.status,
                   CASE con.status
                       WHEN 'CONFIRMED' THEN 'Đã xác nhận'
                       WHEN 'DELIVERED' THEN 'Đã giao xe'
                       WHEN 'RETURNED' THEN 'Đã trả xe'
                       WHEN 'COMPLETED' THEN 'Hoàn thành'
                       WHEN 'CANCELLED' THEN 'Đã hủy'
                       ELSE con.status
                   END AS statusNm
            FROM contract con
            INNER JOIN contract_car cc ON con.id = cc.contract_id
            LEFT JOIN customer cus ON con.customer_id = cus.id
            WHERE cc.car_id = :carId
              AND con.status IN ('CONFIRMED', 'DELIVERED')
              AND (
                  (con.start_date <= :endDate AND con.end_date >= :startDate)
              )
            ORDER BY con.start_date ASC
            """, nativeQuery = true)
    List<ConflictingContractDTO> findConflictingContracts(
            @Param("carId") String carId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );
}

