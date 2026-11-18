package org.example.balance.servicies.domain.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.balance.entities.outbox.BalanceOutbox;
import org.example.balance.repositories.outbox.BalanceOutboxRepository;
import org.example.exceptions.domain.client.NotFound;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor

public class FindBalanceOutboxService {

    private final BalanceOutboxRepository balanceOutboxRepository;

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public BalanceOutbox findById(Long id) {
        return balanceOutboxRepository.findById(id)
                .orElseThrow(() -> new NotFound("Запись не найдена"));
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public List<BalanceOutbox> findNewEventsBatchForUpdate(Pageable pageable, String status) {
        return balanceOutboxRepository.findNewEventsBatchForUpdate(pageable, status);
    }

}
