package org.example.balance.dtos.outbox.payoutappservice;

import java.math.BigDecimal;
import java.time.Instant;

public record PayoutFrozenOutboxDto(
        String transactionUuid,
        Long storeId,
        Long balanceId,
        BigDecimal amount,
        String currency,

        Instant processedAt
) {
}
