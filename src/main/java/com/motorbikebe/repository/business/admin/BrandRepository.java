package com.motorbikebe.repository.business.admin;

import com.motorbikebe.dto.business.admin.brandMng.BrandDTO;
import com.motorbikebe.dto.business.admin.brandMng.BrandSearchDTO;
import com.motorbikebe.entity.domain.BrandEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<BrandEntity, String> {

    @Query(value = """
            SELECT b.id,
                   b.name,
                   b.description
            FROM brand b
            WHERE (:#{#req.keyword} IS NULL OR :#{#req.keyword} = '' 
                   OR b.name LIKE %:#{#req.keyword}% 
                   OR b.description LIKE %:#{#req.keyword}%)
            ORDER BY b.created_date DESC
            """, countQuery = """
            SELECT COUNT(b.id)
            FROM brand b
            WHERE (:#{#req.keyword} IS NULL OR :#{#req.keyword} = '' 
                   OR b.name LIKE %:#{#req.keyword}% 
                   OR b.description LIKE %:#{#req.keyword}%)
            """, nativeQuery = true)
    Page<BrandDTO> searchBrands(Pageable pageable, @Param("req") BrandSearchDTO req);

    BrandEntity findByName(String name);

    List<BrandEntity> findAllByOrderByNameAsc();
}

