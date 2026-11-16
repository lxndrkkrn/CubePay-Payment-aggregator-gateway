package org.example.balance.repositories;

import org.example.balance.entities.BalanceCurrency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceCurrencyRepository extends JpaRepository<BalanceCurrency, Long> {
}
