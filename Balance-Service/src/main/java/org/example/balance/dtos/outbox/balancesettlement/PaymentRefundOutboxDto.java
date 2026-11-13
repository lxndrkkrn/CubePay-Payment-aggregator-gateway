package org.example.balance.dtos.outbox.balancesettlement;

import java.time.Instant;

public record PaymentRefundOutboxDto(
        String refundUuid,
        String originalTransactionUuid,
        Long storeId,

        Instant processedAt
) {
}
