package org.example.merchant.repository;

import jakarta.persistence.LockModeType;
import org.example.merchant.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByUuid(String uuid);
    List<Transaction> findAllByClientTransactionUuid(String clientTransactionUuid);

    List<Transaction> findAllByClientId(String clientId);
    List<Transaction> findAllByClientIp(String clientIp);

    List<Transaction> findAllByStatus(String status);

    Optional<Transaction> findByBankTransactionUuid(String bankTransactionUuid);

    @Query("""
        SELECT COUNT(t) FROM Transaction t 
        WHERE t.status = :status 
          AND t.clientIp = :clientIp 
          AND t.createdAt >= :timeWindow
    """)
    long countByStatusAndIpInTimeWindow(
            @Param("status") String status,
            @Param("clientIp") String clientIp,
            @Param("timeWindow") LocalDateTime timeWindow
    );

    @Query("""
        SELECT COUNT(t) FROM Transaction t 
        WHERE t.status = :status 
          AND t.clientId = :clientId 
          AND t.createdAt >= :timeWindow
    """)
    long countByStatusAndClientIdInTimeWindow(
            @Param("status") String status,
            @Param("clientId") String clientId,
            @Param("timeWindow") LocalDateTime timeWindow
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Transaction t WHERE t.uuid = :uuid")
    Optional<Transaction> findByIdForUpdate(@Param("uuid") String uuid);

}
