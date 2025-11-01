package com.motorbikebe.repository.business.admin;

import com.motorbikebe.dto.business.admin.surchargeTypeMng.SurchargeTypeDTO;
import com.motorbikebe.entity.domain.SurchargeTypeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurchargeTypeRepository extends JpaRepository<SurchargeTypeEntity, String> {

    @Query(value = """
            SELECT st.id,
                   st.name,
                   st.price,
                   st.description,
                   st.status
            FROM surcharge_type st
            WHERE (:#{#req.keyword} IS NULL OR :#{#req.keyword} = ''
                   OR st.name LIKE %:#{#req.keyword}%)
            AND (:#{#req.status} IS NULL OR st.status = :#{#req.status})
            ORDER BY st.created_date DESC
            """, countQuery = """
            SELECT COUNT(st.id)
            FROM surcharge_type st
            WHERE (:#{#req.keyword} IS NULL OR :#{#req.keyword} = ''
                   OR st.name LIKE %:#{#req.keyword}%)
            AND (:#{#req.status} IS NULL OR st.status = :#{#req.status})
            """, nativeQuery = true)
    Page<SurchargeTypeDTO> searchSurchargeTypes(Pageable pageable, @Param("req") com.motorbikebe.dto.business.admin.surchargeTypeMng.SurchargeTypeSearchDTO req);

    SurchargeTypeEntity findByName(String name);

    List<SurchargeTypeEntity> findByStatus(Integer status);
}

