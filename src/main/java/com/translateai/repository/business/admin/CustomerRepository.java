package com.translateai.repository.business.admin;

import com.translateai.dto.business.admin.contractMng.CustomerDTO;
import com.translateai.dto.business.admin.contractMng.CustomerSearchDTO;
import com.translateai.entity.domain.CustomerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, String> {

    @Query(value = """
            SELECT c.id,
                   c.full_name AS fullName,
                   c.phone_number AS phoneNumber,
                   c.email,
                   c.citizen_id AS citizenId,
                   c.address,
                   c.driver_license AS driverLicense
            FROM customer c
            WHERE (:#{#req.fullName} IS NULL OR :#{#req.fullName} = '' OR c.full_name LIKE %:#{#req.fullName}%)
            AND (:#{#req.phoneNumber} IS NULL OR :#{#req.phoneNumber} = '' OR c.phone_number LIKE %:#{#req.phoneNumber}%)
            AND (:#{#req.citizenId} IS NULL OR :#{#req.citizenId} = '' OR c.citizen_id LIKE %:#{#req.citizenId}%)
            ORDER BY c.created_date DESC
            """, countQuery = """
            SELECT COUNT(c.id)
            FROM customer c
            WHERE (:#{#req.fullName} IS NULL OR :#{#req.fullName} = '' OR c.full_name LIKE %:#{#req.fullName}%)
            AND (:#{#req.phoneNumber} IS NULL OR :#{#req.phoneNumber} = '' OR c.phone_number LIKE %:#{#req.phoneNumber}%)
            AND (:#{#req.citizenId} IS NULL OR :#{#req.citizenId} = '' OR c.citizen_id LIKE %:#{#req.citizenId}%)
            """, nativeQuery = true)
    Page<CustomerDTO> searchCustomers(Pageable pageable, @Param("req") CustomerSearchDTO req);

    CustomerEntity findByPhoneNumber(String phoneNumber);

    List<CustomerEntity> findByFullNameContaining(String fullName);
}

