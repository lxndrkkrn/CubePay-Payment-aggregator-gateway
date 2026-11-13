package org.example.balance.dtos.outbox.balancelifecycle;

public record CreateStoreBalanceOutboxDto(
        Long balanceId,
        Long storeId
) {
}
