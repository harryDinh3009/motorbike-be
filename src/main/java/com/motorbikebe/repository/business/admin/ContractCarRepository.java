package com.motorbikebe.repository.business.admin;

import com.motorbikebe.entity.domain.ContractCarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractCarRepository extends JpaRepository<ContractCarEntity, String> {

    /**
     * Lấy danh sách xe theo hợp đồng
     */
    List<ContractCarEntity> findByContractId(String contractId);

    /**
     * Xóa tất cả xe của hợp đồng
     */
    void deleteByContractId(String contractId);

    /**
     * Kiểm tra xe có trong hợp đồng không
     */
    @Query("SELECT CASE WHEN COUNT(cc) > 0 THEN true ELSE false END FROM ContractCarEntity cc WHERE cc.contractId = :contractId AND cc.carId = :carId")
    boolean existsByContractIdAndCarId(@Param("contractId") String contractId, @Param("carId") String carId);

    /**
     * Kiểm tra xe có trong bất kỳ hợp đồng nào không
     */
    @Query("SELECT CASE WHEN COUNT(cc) > 0 THEN true ELSE false END FROM ContractCarEntity cc WHERE cc.carId = :carId")
    boolean existsByCarId(@Param("carId") String carId);
}

