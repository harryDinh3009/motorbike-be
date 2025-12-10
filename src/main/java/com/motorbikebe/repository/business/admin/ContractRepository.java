package com.motorbikebe.repository.business.admin;

import com.motorbikebe.constant.enumconstant.ContractStatus;
import com.motorbikebe.dto.business.admin.contractMng.ContractDTO;
import com.motorbikebe.dto.business.admin.contractMng.ContractSearchDTO;
import com.motorbikebe.dto.business.admin.contractMng.MonthlyRevenueRowDTO;
import com.motorbikebe.entity.domain.ContractEntity;
import com.motorbikebe.repository.projection.ContractRevenueProjection;
import com.motorbikebe.repository.projection.DailyRevenueProjection;
import com.motorbikebe.repository.projection.TopCarRentalProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<ContractEntity, String> {

    /**
     * Tìm kiếm hợp đồng với nhiều điều kiện (query đã nâng cấp)
     * Note: Vì contract có nhiều xe, query này chỉ lấy thông tin cơ bản
     * Danh sách xe, phụ thu, thanh toán sẽ được load riêng
     */
    @Query(value = """
            SELECT con.id,
                   con.contract_code AS contractCode,
                   con.customer_id AS customerId,
                   cus.full_name AS customerName,
                   cus.phone_number AS phoneNumber,
                   cus.email,
                   con.source,
                   con.start_date AS startDate,
                   con.end_date AS endDate,
                   con.pickup_branch_id AS pickupBranchId,
                   pb.name AS pickupBranchName,
                   con.return_branch_id AS returnBranchId,
                   rb.name AS returnBranchName,
                   con.pickup_address AS pickupAddress,
                   con.return_address AS returnAddress,
                   con.total_rental_amount AS totalRentalAmount,
                   con.total_surcharge AS totalSurcharge,
                   con.discount_amount AS discountAmount,
                   con.final_amount AS finalAmount,
                   con.paid_amount AS paidAmount,
                   con.remaining_amount AS remainingAmount,
                   con.status,
                   con.notes,
                   con.delivery_time AS deliveryTime,
                   con.return_time AS returnTime,
                   con.completed_date AS completedDate
            FROM contract con
            INNER JOIN customer cus ON con.customer_id = cus.id
            LEFT JOIN branch pb ON con.pickup_branch_id = pb.id
            LEFT JOIN branch rb ON con.return_branch_id = rb.id
            WHERE (:#{#req.keyword} IS NULL OR :#{#req.keyword} = ''
                   OR con.contract_code LIKE %:#{#req.keyword}%
                   OR cus.full_name LIKE %:#{#req.keyword}%
                   OR cus.phone_number LIKE %:#{#req.keyword}%
                   OR EXISTS (
                       SELECT 1 FROM contract_car cc
                       INNER JOIN car c ON cc.car_id = c.id
                       WHERE cc.contract_id = con.id
                       AND c.license_plate LIKE %:#{#req.keyword}%
                   ))
            AND (:#{#req.status?.name()} IS NULL OR con.status = :#{#req.status?.name()})
            AND (:#{#req.source} IS NULL OR :#{#req.source} = '' OR con.source = :#{#req.source})
            AND (:#{#req.startDateFrom} IS NULL OR con.start_date >= :#{#req.startDateFrom})
            AND (:#{#req.startDateTo} IS NULL OR con.start_date <= :#{#req.startDateTo})
            AND (:#{#req.endDateFrom} IS NULL OR con.end_date >= :#{#req.endDateFrom})
            AND (:#{#req.endDateTo} IS NULL OR con.end_date <= :#{#req.endDateTo})
            AND (:#{#req.pickupBranchId} IS NULL OR :#{#req.pickupBranchId} = '' OR con.pickup_branch_id = :#{#req.pickupBranchId})
            AND (:#{#req.returnBranchId} IS NULL OR :#{#req.returnBranchId} = '' OR con.return_branch_id = :#{#req.returnBranchId})
            ORDER BY con.created_date DESC
            """, countQuery = """
            SELECT COUNT(con.id)
            FROM contract con
            INNER JOIN customer cus ON con.customer_id = cus.id
            WHERE (:#{#req.keyword} IS NULL OR :#{#req.keyword} = ''
                   OR con.contract_code LIKE %:#{#req.keyword}%
                   OR cus.full_name LIKE %:#{#req.keyword}%
                   OR cus.phone_number LIKE %:#{#req.keyword}%
                   OR EXISTS (
                       SELECT 1 FROM contract_car cc
                       INNER JOIN car c ON cc.car_id = c.id
                       WHERE cc.contract_id = con.id
                       AND c.license_plate LIKE %:#{#req.keyword}%
                   ))
            AND (:#{#req.status?.name()} IS NULL OR con.status = :#{#req.status?.name()})
            AND (:#{#req.source} IS NULL OR :#{#req.source} = '' OR con.source = :#{#req.source})
            AND (:#{#req.startDateFrom} IS NULL OR con.start_date >= :#{#req.startDateFrom})
            AND (:#{#req.startDateTo} IS NULL OR con.start_date <= :#{#req.startDateTo})
            AND (:#{#req.endDateFrom} IS NULL OR con.end_date >= :#{#req.endDateFrom})
            AND (:#{#req.endDateTo} IS NULL OR con.end_date <= :#{#req.endDateTo})
            AND (:#{#req.pickupBranchId} IS NULL OR :#{#req.pickupBranchId} = '' OR con.pickup_branch_id = :#{#req.pickupBranchId})
            AND (:#{#req.returnBranchId} IS NULL OR :#{#req.returnBranchId} = '' OR con.return_branch_id = :#{#req.returnBranchId})
            """, nativeQuery = true)
    Page<ContractDTO> searchContracts(Pageable pageable, @Param("req") ContractSearchDTO req);

    /**
     * Tìm hợp đồng theo khách hàng
     */
    List<ContractEntity> findByCustomerId(String customerId);

    /**
     * Kiểm tra có hợp đồng nào dùng branch làm pickup hoặc return branch không
     */
    @Query(value = """
            SELECT COUNT(con.id)
            FROM contract con
            WHERE (con.pickup_branch_id = :branchId OR con.return_branch_id = :branchId)
            """, nativeQuery = true)
    Long existsByPickupBranchIdOrReturnBranchId(@Param("branchId") String branchId);

    /**
     * Kiểm tra có hợp đồng nào dùng user làm delivery hoặc return user không
     */
    @Query(value = """
            SELECT COUNT(con.id)
            FROM contract con
            WHERE (con.delivery_user_id = :userId OR con.return_user_id = :userId)
            """, nativeQuery = true)
    Long existsByDeliveryUserIdOrReturnUserId(@Param("userId") String userId);

    /**
     * Tìm hợp đồng theo trạng thái
     */
    List<ContractEntity> findByStatus(ContractStatus status);

    /**
     * Tìm hợp đồng có chứa xe cụ thể và trong khoảng thời gian
     * (để kiểm tra xe có available không)
     */
    @Query(value = """
            SELECT con.* FROM contract con
            INNER JOIN contract_car cc ON con.id = cc.contract_id
            WHERE cc.car_id = :carId
            AND con.status IN ('CONFIRMED', 'DELIVERED')
            AND con.start_date <= :endDate
            AND con.end_date >= :startDate
            """, nativeQuery = true)
    List<ContractEntity> findOverlappingContracts(@Param("carId") String carId,
                                                    @Param("startDate") Date startDate,
                                                    @Param("endDate") Date endDate);

    /**
     * Tìm hợp đồng gần nhất trong quá khứ của một xe chưa được trả (status không phải RETURNED, COMPLETED, CANCELLED)
     * Chỉ tìm hợp đồng có start_date < currentContractStartDate (trước ngày thuê của hợp đồng hiện tại)
     * Sắp xếp theo start_date DESC để lấy hợp đồng gần nhất trong quá khứ
     */
    @Query(value = """
            SELECT con.* FROM contract con
            INNER JOIN contract_car cc ON con.id = cc.contract_id
            WHERE cc.car_id = :carId
            AND con.id != :excludeContractId
            AND con.status NOT IN ('RETURNED', 'COMPLETED', 'CANCELLED')
            AND con.start_date < :currentContractStartDate
            ORDER BY con.start_date DESC
            LIMIT 1
            """, nativeQuery = true)
    List<ContractEntity> findLatestUnreturnedContractByCarId(
            @Param("carId") String carId,
            @Param("excludeContractId") String excludeContractId,
            @Param("currentContractStartDate") Date currentContractStartDate
    );

    /**
     * Generate mã hợp đồng mới (HDxxxx)
     */
    @Query(value = "SELECT CONCAT('HD', LPAD(IFNULL(MAX(CAST(SUBSTRING(contract_code, 3) AS UNSIGNED)), 0) + 1, 6, '0')) FROM contract WHERE contract_code LIKE 'HD%'", nativeQuery = true)
    String generateContractCode();

    @Query(value = """
            SELECT COUNT(con.id)
            FROM contract con
            WHERE con.status <> 'CANCELLED'
              AND (:branchId IS NULL OR :branchId = '' OR con.pickup_branch_id = :branchId)
              AND con.start_date >= :startDate
              AND con.start_date < :endDate
            """, nativeQuery = true)
    long countContractsByBranchAndDate(@Param("branchId") String branchId,
                                       @Param("startDate") Date startDate,
                                       @Param("endDate") Date endDate);

    /**
     * Đếm số xe đã thuê trong tháng (đếm số bản ghi contract_car từ các contract có status <> 'CANCELLED')
     */
    @Query(value = """
            SELECT COUNT(cc.id)
            FROM contract_car cc
            INNER JOIN contract con ON cc.contract_id = con.id
            WHERE con.status <> 'CANCELLED'
              AND (:branchId IS NULL OR :branchId = '' OR con.pickup_branch_id = :branchId)
              AND con.start_date >= :startDate
              AND con.start_date < :endDate
            """, nativeQuery = true)
    long countRentedCarsByBranchAndDate(@Param("branchId") String branchId,
                                        @Param("startDate") Date startDate,
                                        @Param("endDate") Date endDate);

    @Query(value = """
            SELECT 
                COALESCE(SUM(con.final_amount), 0) AS contractAmount,
                COALESCE(SUM(con.total_rental_amount), 0) AS rentalAmount,
                COALESCE(SUM(con.total_surcharge), 0) AS surchargeAmount
            FROM contract con
            WHERE con.status <> 'CANCELLED'
              AND (:branchId IS NULL OR :branchId = '' OR con.pickup_branch_id = :branchId)
              AND con.start_date >= :startDate
              AND con.start_date < :endDate
            """, nativeQuery = true)
    ContractRevenueProjection sumRevenueByBranchAndDate(@Param("branchId") String branchId,
                                                         @Param("startDate") Date startDate,
                                                         @Param("endDate") Date endDate);

    @Query(value = """
            SELECT 
                DATE(con.start_date) AS revenueDate,
                COALESCE(SUM(con.final_amount), 0) AS contractAmount,
                COALESCE(SUM(con.total_rental_amount), 0) AS rentalAmount,
                COALESCE(SUM(con.total_surcharge), 0) AS surchargeAmount
            FROM contract con
            WHERE con.status <> 'CANCELLED'
              AND (:branchId IS NULL OR :branchId = '' OR con.pickup_branch_id = :branchId OR con.return_branch_id = :branchId)
              AND con.start_date >= :startDate
              AND con.start_date < :endDate
            GROUP BY DATE(con.start_date)
            ORDER BY revenueDate
            """, nativeQuery = true)
    List<DailyRevenueProjection> sumDailyRevenueByBranchAndDate(@Param("branchId") String branchId,
                                                                @Param("startDate") Date startDate,
                                                                @Param("endDate") Date endDate);

    /**
     * Thống kê doanh thu theo tháng (dựa theo ngày hoàn thành, status = COMPLETED)
     */
    @Query(value = """
            SELECT 
                MONTH(con.completed_date) AS month,
                COUNT(con.id) AS contractCount,
                COALESCE(SUM(con.total_rental_amount), 0) AS rentalAmount,
                COALESCE(SUM(con.total_surcharge), 0) AS surchargeAmount,
                COALESCE(SUM(con.discount_amount), 0) AS discountAmount
            FROM contract con
            WHERE YEAR(con.completed_date) = :year
              AND con.status = 'COMPLETED'
              AND con.completed_date IS NOT NULL
              AND (:branchId IS NULL OR :branchId = '' OR con.pickup_branch_id = :branchId OR con.return_branch_id = :branchId)
            GROUP BY MONTH(con.completed_date)
            ORDER BY month
            """, nativeQuery = true)
    List<Object[]> sumMonthlyRevenue(@Param("year") int year, @Param("branchId") String branchId);

    /**
     * Lấy doanh thu theo ngày hoàn thành (completed_date) với status = COMPLETED
     */
    @Query(value = """
            SELECT 
                DATE(con.completed_date) AS completedDate,
                COUNT(con.id) AS contractCount,
                COALESCE(SUM(con.total_rental_amount), 0) AS rentalAmount,
                COALESCE(SUM(con.total_surcharge), 0) AS surchargeAmount,
                COALESCE(SUM(con.discount_amount), 0) AS discountAmount
            FROM contract con
            WHERE con.status = 'COMPLETED'
              AND con.completed_date IS NOT NULL
              AND DATE(con.completed_date) >= DATE(:startDate)
              AND DATE(con.completed_date) <= DATE(:endDate)
              AND (:branchId IS NULL OR :branchId = '' OR con.pickup_branch_id = :branchId)
            GROUP BY DATE(con.completed_date)
            ORDER BY completedDate
            """, nativeQuery = true)
    List<Object[]> sumDailyRevenueByCompletedDate(@Param("branchId") String branchId,
                                                   @Param("startDate") Date startDate,
                                                   @Param("endDate") Date endDate);

    /**
     * Lấy top 5 xe được thuê nhiều nhất (dựa theo ngày hoàn thành, status = COMPLETED)
     * Logic giống với thống kê lượt thuê theo mẫu xe
     */
    @Query(value = """
            SELECT 
                c.model AS model,
                COUNT(cc.id) AS rentalCount,
                COALESCE(SUM(cc.total_amount), 0) AS revenue
            FROM contract_car cc
            INNER JOIN contract con ON cc.contract_id = con.id
            INNER JOIN car c ON cc.car_id = c.id
            WHERE con.status = 'COMPLETED'
              AND con.completed_date IS NOT NULL
              AND DATE(con.completed_date) >= DATE(:startDate)
              AND DATE(con.completed_date) <= DATE(:endDate)
              AND (:branchId IS NULL OR :branchId = '' OR con.pickup_branch_id = :branchId)
            GROUP BY c.model
            ORDER BY rentalCount DESC, revenue DESC
            LIMIT 5
            """, nativeQuery = true)
    List<TopCarRentalProjection> findTop5RentedCars(@Param("branchId") String branchId,
                                                     @Param("startDate") Date startDate,
                                                     @Param("endDate") Date endDate);

    /**
     * Thống kê lượt thuê theo mẫu xe (dựa theo ngày hoàn thành, status = COMPLETED)
     * Chỉ lấy mẫu xe đã được thuê trong khoảng thời gian
     */
    @Query(value = """
            SELECT 
                c.model AS modelName,
                COUNT(cc.id) AS rentalCount,
                COALESCE(SUM(cc.total_amount), 0) AS rentalAmount
            FROM contract_car cc
            INNER JOIN contract con ON cc.contract_id = con.id
            INNER JOIN car c ON cc.car_id = c.id
            WHERE con.status = 'COMPLETED'
              AND con.completed_date IS NOT NULL
              AND DATE(con.completed_date) >= DATE(:startDate)
              AND DATE(con.completed_date) <= DATE(:endDate)
              AND (:branchId IS NULL OR :branchId = '' OR con.pickup_branch_id = :branchId OR con.return_branch_id = :branchId)
            GROUP BY c.model
            ORDER BY rentalCount DESC, rentalAmount DESC
            """, nativeQuery = true)
    List<Object[]> sumRentalByModel(@Param("branchId") String branchId,
                                     @Param("startDate") Date startDate,
                                     @Param("endDate") Date endDate);

    /**
     * Lấy tất cả mẫu xe DISTINCT theo chi nhánh
     */
    @Query(value = """
            SELECT DISTINCT c.model 
            FROM car c 
            WHERE (:branchId IS NULL OR :branchId = '' OR c.branch_id = :branchId)
            ORDER BY c.model ASC
            """, nativeQuery = true)
    List<String> findAllDistinctModels(@Param("branchId") String branchId);

    /**
     * Lấy dữ liệu lịch đặt xe
     * Query lấy tất cả contract_car trong khoảng thời gian với filter theo branch và status
     * Mỗi row = 1 xe trong hợp đồng (contract_car)
     * 
     * Logic filter:
     * - Hợp đồng overlap với khoảng thời gian: start_date < endDate AND end_date >= startDate
     * - Filter theo branchId (pickup_branch_id)
     * - Filter theo status (nếu không null/empty)
     * - Loại bỏ hợp đồng CANCELLED
     */
    @Query(value = """
            SELECT 
                cc.id AS contractCarId,
                con.id AS contractId,
                con.contract_code AS contractCode,
                cc.car_id AS carId,
                c.model AS carModel,
                c.license_plate AS licensePlate,
                cus.full_name AS customerName,
                cus.phone_number AS customerPhone,
                con.start_date AS startDate,
                con.end_date AS endDate,
                con.status AS status,
                con.pickup_branch_id AS pickupBranchId
            FROM contract_car cc
            INNER JOIN contract con ON cc.contract_id = con.id
            INNER JOIN car c ON cc.car_id = c.id
            INNER JOIN customer cus ON con.customer_id = cus.id
            WHERE con.status <> 'CANCELLED'
              AND (:branchId IS NULL OR :branchId = '' OR con.pickup_branch_id = :branchId)
              AND (:status IS NULL OR :status = '' OR con.status = :status)
              AND con.start_date < :endDate
              AND con.end_date >= :startDate
            ORDER BY c.model ASC, c.license_plate ASC, con.start_date ASC
            """, nativeQuery = true)
    List<Object[]> findContractScheduleItems(@Param("branchId") String branchId,
                                             @Param("status") String status,
                                             @Param("startDate") Date startDate,
                                             @Param("endDate") Date endDate);

}


