package org.example.balance.repositories;

import org.example.balance.entities.HistoryBalance;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Optional;

public interface HistoryBalanceRepository extends JpaRepository<HistoryBalance, Long> {

    Optional<HistoryBalance> findByUuid(String uuid);

    boolean existsByIdempotencyKey(String idempotencyKey);

    // Выписка по счету для личного кабинета мерчанта
    Page<HistoryBalance> findAllByBalanceIdOrderByCreatedAtDesc(Long balanceId, Pageable pageable);

    // Выписка за конкретный период (для сверки / reconciliation)
    Page<HistoryBalance> findAllByBalanceIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long balanceId,
            Instant start,
            Instant end,
            Pageable pageable
    );

}
