package org.example.balance.repositories;

import jakarta.persistence.LockModeType;
import org.example.balance.entities.Balance;
import org.example.balance.entities.BalanceCurrency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BalanceCurrencyRepository extends JpaRepository<BalanceCurrency, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM BalanceCurrency c WHERE c.id = :id")
    Optional<BalanceCurrency> findByIdForUpdate(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM BalanceCurrency c")
    List<BalanceCurrency> findAllForUpdate();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM BalanceCurrency c WHERE c.currency = :currency")
    Optional<BalanceCurrency> findByCurrencyForUpdate(@Param("currency") BalanceCurrency currency);

    Optional<BalanceCurrency> findByCurrency(BalanceCurrency currency);

}
