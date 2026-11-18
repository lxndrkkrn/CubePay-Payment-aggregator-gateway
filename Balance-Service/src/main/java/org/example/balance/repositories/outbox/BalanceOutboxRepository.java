package org.example.balance.repositories.outbox;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.example.balance.entities.BalanceCurrency;
import org.example.balance.entities.outbox.BalanceOutbox;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BalanceOutboxRepository extends JpaRepository<BalanceOutbox, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
            @QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2")
    })
    @Query("SELECT o FROM BalanceOutbox o WHERE o.status = :status ORDER BY o.createdAt ASC")
    List<BalanceOutbox> findNewEventsBatchForUpdate(Pageable pageable, @Param("status") String status);

    @Modifying
    @Query("DELETE FROM BalanceOutbox o WHERE o.status = 'PROCESSED' AND o.createdAt < :threshold")
    int deleteOldProcessedEvents(@Param("threshold") LocalDateTime threshold);
}

