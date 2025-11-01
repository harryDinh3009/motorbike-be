package com.motorbikebe.repository.business.admin;

import com.motorbikebe.dto.business.admin.employeeMng.EmployeeDTO;
import com.motorbikebe.dto.business.admin.employeeMng.EmployeeSearchDTO;
import com.motorbikebe.entity.domain.EmployeeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, String> {

    @Query(value = """
            SELECT e.id,
                   e.full_name AS fullName,
                   e.phone_number AS phoneNumber,
                   e.email,
                   e.date_of_birth AS dateOfBirth,
                   e.gender,
                   e.address,
                   e.branch_id AS branchId,
                   b.name AS branchName,
                   e.role,
                   e.status
            FROM employee e
            LEFT JOIN branch b ON e.branch_id = b.id
            WHERE (:#{#req.keyword} IS NULL OR :#{#req.keyword} = '' 
                   OR e.full_name LIKE %:#{#req.keyword}% 
                   OR e.phone_number LIKE %:#{#req.keyword}%
                   OR e.email LIKE %:#{#req.keyword}%)
            AND (:#{#req.branchId} IS NULL OR :#{#req.branchId} = '' OR e.branch_id = :#{#req.branchId})
            AND (:#{#req.role} IS NULL OR :#{#req.role} = '' OR e.role = :#{#req.role})
            AND (:#{#req.status} IS NULL OR e.status = :#{#req.status})
            ORDER BY e.created_date DESC
            """, countQuery = """
            SELECT COUNT(e.id)
            FROM employee e
            LEFT JOIN branch b ON e.branch_id = b.id
            WHERE (:#{#req.keyword} IS NULL OR :#{#req.keyword} = '' 
                   OR e.full_name LIKE %:#{#req.keyword}% 
                   OR e.phone_number LIKE %:#{#req.keyword}%
                   OR e.email LIKE %:#{#req.keyword}%)
            AND (:#{#req.branchId} IS NULL OR :#{#req.branchId} = '' OR e.branch_id = :#{#req.branchId})
            AND (:#{#req.role} IS NULL OR :#{#req.role} = '' OR e.role = :#{#req.role})
            AND (:#{#req.status} IS NULL OR e.status = :#{#req.status})
            """, nativeQuery = true)
    Page<EmployeeDTO> searchEmployees(Pageable pageable, @Param("req") EmployeeSearchDTO req);

    List<EmployeeEntity> findByBranchId(String branchId);

    EmployeeEntity findByPhoneNumber(String phoneNumber);

    EmployeeEntity findByEmail(String email);
}

