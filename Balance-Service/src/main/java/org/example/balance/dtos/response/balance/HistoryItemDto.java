package org.example.balance.dtos.response.balance;

import java.math.BigDecimal;
import java.time.Instant;

public record HistoryItemDto(
        String uuid,
        String operationType,
        String bucketType,
        BigDecimal amount,
        BigDecimal balanceBefore,
        BigDecimal balanceAfter,
        String description,
        Instant createdAt
) {
}
