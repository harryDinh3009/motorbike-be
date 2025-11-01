package com.motorbikebe.repository.business.admin;

import com.motorbikebe.entity.domain.ContractImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractImageRepository extends JpaRepository<ContractImageEntity, String> {

    /**
     * Lấy tất cả ảnh của hợp đồng
     */
    List<ContractImageEntity> findByContractId(String contractId);

    /**
     * Lấy ảnh theo loại (DELIVERY / RETURN)
     */
    List<ContractImageEntity> findByContractIdAndImageType(String contractId, String imageType);

    /**
     * Xóa tất cả ảnh của hợp đồng
     */
    void deleteByContractId(String contractId);

    /**
     * Xóa ảnh theo loại
     */
    void deleteByContractIdAndImageType(String contractId, String imageType);

    /**
     * Đếm số ảnh theo loại
     */
    @Query("SELECT COUNT(ci) FROM ContractImageEntity ci WHERE ci.contractId = :contractId AND ci.imageType = :imageType")
    long countByContractIdAndImageType(@Param("contractId") String contractId, @Param("imageType") String imageType);
}

