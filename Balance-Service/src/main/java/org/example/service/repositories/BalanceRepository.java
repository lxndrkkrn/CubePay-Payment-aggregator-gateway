package org.example.service.repositories;

import jakarta.persistence.LockModeType;
import org.example.service.entities.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BalanceRepository extends JpaRepository<Balance, Long> {

    Optional<Balance> findByUuid(String uuid);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Balance b WHERE b.uuid = :uuid")
    Optional<Balance> findByUuidForUpdate(@Param("uuid") String uuid);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Balance b WHERE b.id = :id")
    Optional<Balance> findByIdForUpdate(@Param("id") Long id);

    Optional<Balance> findByStoreId(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Balance b WHERE b.storeId = :storeId")
    Optional<Balance> findByStoreIdForUpdate(@Param("storeId") Long storeId);

    Optional<Balance> findByStatus(String status);

}
