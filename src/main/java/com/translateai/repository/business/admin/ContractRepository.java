package com.translateai.repository.business.admin;

import com.translateai.constant.enumconstant.ContractStatus;
import com.translateai.dto.business.admin.contractMng.ContractDTO;
import com.translateai.dto.business.admin.contractMng.ContractSearchDTO;
import com.translateai.entity.domain.ContractEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<ContractEntity, String> {

    @Query(value = """
            SELECT ROW_NUMBER() OVER (ORDER BY con.created_date DESC) AS rowNum,
                   con.id,
                   con.car_id AS carId,
                   car.name AS carName,
                   car.license_plate AS licensePlate,
                   con.customer_id AS customerId,
                   cus.full_name AS customerName,
                   cus.phone_number AS phoneNumber,
                   con.start_date AS startDate,
                   con.end_date AS endDate,
                   con.rental_days AS rentalDays,
                   con.daily_price AS dailyPrice,
                   con.total_amount AS totalAmount,
                   con.surcharge_amount AS surchargeAmount,
                   con.final_amount AS finalAmount,
                   con.status,
                   con.notes,
                   con.actual_end_date AS actualEndDate
            FROM contract con
            INNER JOIN car car ON con.car_id = car.id
            INNER JOIN customer cus ON con.customer_id = cus.id
            WHERE (:#{#req.customerId} IS NULL OR :#{#req.customerId} = '' OR con.customer_id = :#{#req.customerId})
            AND (:#{#req.carId} IS NULL OR :#{#req.carId} = '' OR con.car_id = :#{#req.carId})
            AND (:#{#req.status} IS NULL OR con.status = :#{#req.status})
            AND (:#{#req.startDateFrom} IS NULL OR con.start_date >= :#{#req.startDateFrom})
            AND (:#{#req.startDateTo} IS NULL OR con.start_date <= :#{#req.startDateTo})
            ORDER BY con.created_date DESC
            """, countQuery = """
            SELECT COUNT(con.id)
            FROM contract con
            WHERE (:#{#req.customerId} IS NULL OR :#{#req.customerId} = '' OR con.customer_id = :#{#req.customerId})
            AND (:#{#req.carId} IS NULL OR :#{#req.carId} = '' OR con.car_id = :#{#req.carId})
            AND (:#{#req.status} IS NULL OR con.status = :#{#req.status})
            AND (:#{#req.startDateFrom} IS NULL OR con.start_date >= :#{#req.startDateFrom})
            AND (:#{#req.startDateTo} IS NULL OR con.start_date <= :#{#req.startDateTo})
            """, nativeQuery = true)
    Page<ContractDTO> searchContracts(Pageable pageable, @Param("req") ContractSearchDTO req);

    List<ContractEntity> findByCarIdAndStatus(String carId, ContractStatus status);

    List<ContractEntity> findByCustomerId(String customerId);

    @Query("SELECT c FROM ContractEntity c WHERE c.carId = :carId AND c.status = :status AND c.endDate >= :currentDate")
    List<ContractEntity> findActiveContractsByCarId(@Param("carId") String carId, 
                                                      @Param("status") ContractStatus status, 
                                                      @Param("currentDate") Long currentDate);
}

