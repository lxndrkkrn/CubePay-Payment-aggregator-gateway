package org.example.balance.dtos.response.balance;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public record StoreBalanceInfoResponse(
        Long storeId,
        Long balanceId,
        String balanceUuid,
        String status,
        BigDecimal availableBalance,
        BigDecimal holdBalance,
        Map<String, BigDecimal> multiplyExchangeRate,
        LocalDateTime getAt
) {
}
