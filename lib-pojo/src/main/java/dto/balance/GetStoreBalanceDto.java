package dto.balance;

public record GetStoreBalanceDto(
        Long storeId,
        Long balanceId,
        String balanceUuid
) {
}
