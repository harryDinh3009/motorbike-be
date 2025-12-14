package com.motorbikebe.repository.business.admin;

import com.motorbikebe.dto.business.admin.customerMng.CustomerDTO;
import com.motorbikebe.dto.business.admin.customerMng.CustomerSearchDTO;
import com.motorbikebe.entity.domain.CustomerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, String> {

    @Query(value = """
            SELECT c.id,
                   c.full_name AS fullName,
                   c.phone_number AS phoneNumber,
                   c.email,
                   c.date_of_birth AS dateOfBirth,
                   c.gender,
                   c.country,
                   c.address,
                   c.citizen_id AS citizenId,
                   c.citizen_id_front_image_url AS citizenIdFrontImageUrl,
                   c.citizen_id_back_image_url AS citizenIdBackImageUrl,
                   c.driver_license AS driverLicense,
                   c.driver_license_image_url AS driverLicenseImageUrl,
                   c.passport,
                   c.passport_image_url AS passportImageUrl,
                   c.note,
                   c.total_spent AS totalSpent
            FROM customer c
            WHERE (:#{#req.keyword} IS NULL OR :#{#req.keyword} = '' 
                   OR c.full_name LIKE %:#{#req.keyword}% 
                   OR c.phone_number LIKE %:#{#req.keyword}%
                   OR c.email LIKE %:#{#req.keyword}%)
            AND (:#{#req.country} IS NULL OR :#{#req.country} = '' OR c.country = :#{#req.country})
            ORDER BY c.created_date DESC
            """, countQuery = """
            SELECT COUNT(c.id)
            FROM customer c
            WHERE (:#{#req.keyword} IS NULL OR :#{#req.keyword} = '' 
                   OR c.full_name LIKE %:#{#req.keyword}% 
                   OR c.phone_number LIKE %:#{#req.keyword}%
                   OR c.email LIKE %:#{#req.keyword}%)
            AND (:#{#req.country} IS NULL OR :#{#req.country} = '' OR c.country = :#{#req.country})
            """, nativeQuery = true)
    Page<CustomerDTO> searchCustomers(Pageable pageable, @Param("req") CustomerSearchDTO req);

    CustomerEntity findByPhoneNumber(String phoneNumber);

    CustomerEntity findByEmail(String email);

    List<CustomerEntity> findByCountry(String country);

    /**
     * Đếm số khách hàng mới trong tháng (theo created_date)
     */
    @Query(value = """
            SELECT COUNT(c.id)
            FROM customer c
            WHERE FROM_UNIXTIME(c.created_date / 1000) >= :startDate
              AND FROM_UNIXTIME(c.created_date / 1000) < :endDate
            """, nativeQuery = true)
    long countNewCustomersByDate(@Param("startDate") Date startDate,
                                  @Param("endDate") Date endDate);
}

