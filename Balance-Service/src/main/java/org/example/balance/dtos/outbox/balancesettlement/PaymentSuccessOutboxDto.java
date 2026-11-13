package org.example.balance.dtos.outbox.balancesettlement;

import java.time.Instant;

public record PaymentSuccessOutboxDto(
        String transactionalUuid,
        Long storeId,

        Instant processedAt
) {
}
