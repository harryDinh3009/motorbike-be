package com.motorbikebe.repository.business.admin;

import com.motorbikebe.dto.business.admin.contractMng.PaymentTransactionDTO;
import com.motorbikebe.entity.domain.PaymentTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransactionEntity, String> {

    /**
     * Lấy danh sách thanh toán theo hợp đồng (với thông tin nhân viên)
     */
    @Query(value = """
            SELECT pt.id,
                   pt.transaction_code AS transactionCode,
                   pt.contract_id AS contractId,
                   pt.payment_method AS paymentMethod,
                   pt.amount,
                   pt.payment_date AS paymentDate,
                   pt.employee_id AS employeeId,
                   e.full_name AS employeeName,
                   pt.notes,
                   pt.status
            FROM payment_transaction pt
            LEFT JOIN employee e ON pt.employee_id = e.id
            WHERE pt.contract_id = :contractId
            ORDER BY pt.payment_date DESC
            """, nativeQuery = true)
    List<PaymentTransactionDTO> findByContractIdWithEmployee(@Param("contractId") String contractId);

    /**
     * Tính tổng số tiền đã thanh toán của hợp đồng
     */
    @Query("SELECT COALESCE(SUM(pt.amount), 0) FROM PaymentTransactionEntity pt WHERE pt.contractId = :contractId AND pt.status = 'SUCCESS'")
    BigDecimal sumAmountByContractId(@Param("contractId") String contractId);

    /**
     * Xóa tất cả thanh toán của hợp đồng
     */
    void deleteByContractId(String contractId);
}

