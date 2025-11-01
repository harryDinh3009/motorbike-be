package com.motorbikebe.repository.business.admin;

import com.motorbikebe.dto.business.admin.branchMng.BranchDTO;
import com.motorbikebe.dto.business.admin.branchMng.BranchSearchDTO;
import com.motorbikebe.entity.domain.BranchEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchRepository extends JpaRepository<BranchEntity, String> {

    @Query(value = """
            SELECT b.id,
                   b.name,
                   b.phone_number AS phoneNumber,
                   b.address,
                   b.note,
                   b.status
            FROM branch b
            WHERE (:#{#req.keyword} IS NULL OR :#{#req.keyword} = '' 
                   OR b.name LIKE %:#{#req.keyword}% 
                   OR b.phone_number LIKE %:#{#req.keyword}%)
            AND (:#{#req.status} IS NULL OR b.status = :#{#req.status})
            ORDER BY b.created_date DESC
            """, countQuery = """
            SELECT COUNT(b.id)
            FROM branch b
            WHERE (:#{#req.keyword} IS NULL OR :#{#req.keyword} = '' 
                   OR b.name LIKE %:#{#req.keyword}% 
                   OR b.phone_number LIKE %:#{#req.keyword}%)
            AND (:#{#req.status} IS NULL OR b.status = :#{#req.status})
            """, nativeQuery = true)
    Page<BranchDTO> searchBranches(Pageable pageable, @Param("req") BranchSearchDTO req);

    List<BranchEntity> findByStatus(Integer status);

    BranchEntity findByPhoneNumber(String phoneNumber);
}

