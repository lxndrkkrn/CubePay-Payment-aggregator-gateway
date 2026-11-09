package org.example.balance.servicies.domain.history;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.domain.client.NotFound;
import org.example.balance.entities.HistoryBalance;
import org.example.balance.repositories.HistoryBalanceRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor

public class FindHistoryBalanceService {

    private final HistoryBalanceRepository historyBalanceRepository;

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public HistoryBalance findByUuid(String uuid) {
        return historyBalanceRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFound("Запись не найдена"));
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public boolean existsByIdempotencyKey(String idempotencyKey) {
        return historyBalanceRepository.existsByIdempotencyKey(idempotencyKey);
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public Page<HistoryBalance> findAllByBalanceIdOrderByCreatedAtDesc(Long balanceId, Pageable pageable) {
        return historyBalanceRepository.findAllByBalanceIdOrderByCreatedAtDesc(balanceId, pageable);
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public Page<HistoryBalance> findAllByBalanceIdAndCreatedAtBetweenOrderByCreatedAtDesc(Long balanceId, Instant start, Instant end, Pageable pageable) {
        return historyBalanceRepository.findAllByBalanceIdAndCreatedAtBetweenOrderByCreatedAtDesc(balanceId, start, end, pageable);
    }

}
