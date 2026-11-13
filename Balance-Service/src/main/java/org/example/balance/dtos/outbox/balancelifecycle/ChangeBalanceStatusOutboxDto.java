package org.example.balance.dtos.outbox.balancelifecycle;

public record ChangeBalanceStatusOutboxDto(
        Long balanceId,
        String status
) {
}
