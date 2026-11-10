package org.example.balance.repositories.outbox;

import org.example.balance.entities.outbox.BalanceOutbox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceOutboxRepository extends JpaRepository<BalanceOutbox, Long> {
}
