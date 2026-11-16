package org.example.balance.dtos.outbox.payoutappservice;

import java.math.BigDecimal;
import java.time.Instant;

public record CancelPayoutOutboxDto(
        String transactionUuid,
        String requestTransaction,
        Long storeId,
        Long balanceId,

        BigDecimal amount,
        String currency,

        String cancelDescription,

        Instant processedAt
) {
}
