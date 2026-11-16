package dto.balance;

import java.time.LocalDateTime;

public record GetBalanceHistoryByPeriodAndPageDto(
        Long storeId,
        Long balanceId,
        String balanceUuid,
        Integer page,
        Integer pageSize,
        LocalDateTime start,
        LocalDateTime end
) {
}
