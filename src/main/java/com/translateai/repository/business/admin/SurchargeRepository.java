package com.translateai.repository.business.admin;

import com.translateai.dto.business.admin.contractMng.SurchargeDTO;
import com.translateai.entity.domain.SurchargeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurchargeRepository extends JpaRepository<SurchargeEntity, String> {

    @Query(value = """
            SELECT s.id,
                   s.contract_id AS contractId,
                   s.description,
                   s.amount,
                   s.notes
            FROM surcharge s
            WHERE s.contract_id = :contractId
            ORDER BY s.created_date DESC
            """, nativeQuery = true)
    List<SurchargeDTO> findByContractId(@Param("contractId") String contractId);

    void deleteByContractId(String contractId);
}

