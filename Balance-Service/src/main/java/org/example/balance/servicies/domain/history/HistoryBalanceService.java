package org.example.balance.servicies.domain.history;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.domain.client.BadRequest;
import org.example.balance.entities.HistoryBalance;
import org.example.balance.enums.history.HistoryBucketType;
import org.example.balance.enums.history.HistoryOperationType;
import org.example.balance.repositories.HistoryBalanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor

public class HistoryBalanceService {

    private final HistoryBalanceRepository historyBalanceRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public HistoryBalance createBalanceLog(Long balanceId, HistoryOperationType operationType, HistoryBucketType bucketType, BigDecimal amount, BigDecimal balanceBefore, BigDecimal balanceAfter, String description, String idempotencyKey) {
        log.info("Создание новой записи - Balance ID: {}; {} ({}), Amount: {}; Description: {}; idempotencyKey: {}", balanceId, operationType, bucketType, amount, description, idempotencyKey);

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new BadRequest("Ключ идемпотентности обязателен для финансовой проводки");
        }

        HistoryBalance history = new HistoryBalance();

        history.setBalanceId(balanceId);
        history.setIdempotencyKey(idempotencyKey);
        history.setOperationType(operationType);
        history.setBucketType(bucketType);
        history.setAmount(amount);
        history.setBalanceBefore(balanceBefore);
        history.setBalanceAfter(balanceAfter);
        history.setDescription(description);

        HistoryBalance saved = historyBalanceRepository.save(history);
        log.info("Записана проводка UUID: {} для баланса ID: {} ({} {}, сумма: {})", saved.getUuid(), balanceId, operationType, bucketType, amount);

        return saved;
    }

}
