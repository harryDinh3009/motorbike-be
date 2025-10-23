package com.translateai.repository.business.admin;

import com.translateai.constant.enumconstant.CarStatus;
import com.translateai.dto.business.admin.contractMng.CarDTO;
import com.translateai.dto.business.admin.contractMng.CarSearchDTO;
import com.translateai.entity.domain.CarEntity;
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
                   c.name,
                   c.license_plate AS licensePlate,
                   c.car_type AS carType,
                   c.daily_price AS dailyPrice,
                   c.status,
                   c.image_url AS imageUrl,
                   c.description
            FROM car c
            WHERE (:#{#req.name} IS NULL OR :#{#req.name} = '' OR c.name LIKE %:#{#req.name}%)
            AND (:#{#req.licensePlate} IS NULL OR :#{#req.licensePlate} = '' OR c.license_plate LIKE %:#{#req.licensePlate}%)
            AND (:#{#req.status} IS NULL OR c.status = :#{#req.status})
            ORDER BY c.created_date DESC
            """, countQuery = """
            SELECT COUNT(c.id)
            FROM car c
            WHERE (:#{#req.name} IS NULL OR :#{#req.name} = '' OR c.name LIKE %:#{#req.name}%)
            AND (:#{#req.licensePlate} IS NULL OR :#{#req.licensePlate} = '' OR c.license_plate LIKE %:#{#req.licensePlate}%)
            AND (:#{#req.status} IS NULL OR c.status = :#{#req.status})
            """, nativeQuery = true)
    Page<CarDTO> searchCars(Pageable pageable, @Param("req") CarSearchDTO req);

    CarEntity findByLicensePlate(String licensePlate);

    List<CarEntity> findByStatus(CarStatus status);
}

