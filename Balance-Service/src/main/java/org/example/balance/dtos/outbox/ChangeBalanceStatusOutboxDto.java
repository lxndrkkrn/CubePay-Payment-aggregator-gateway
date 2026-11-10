package org.example.balance.dtos.outbox;

public record ChangeBalanceStatusOutboxDto(
        Long balanceId,
        String status
) {
}
