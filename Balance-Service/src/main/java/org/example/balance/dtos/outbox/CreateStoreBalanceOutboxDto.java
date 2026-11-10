package org.example.balance.dtos.outbox;

public record CreateStoreBalanceOutboxDto(
        Long balanceId,
        Long storeId
) {
}
