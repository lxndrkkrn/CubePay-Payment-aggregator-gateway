package org.example.balance.dtos.response.balance;

import org.example.balance.entities.HistoryBalance;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

public record BalanceHistoryByPeriodAndPageDto(
        Long storeId,
        Long balanceId,
        Integer page,
        Integer pageSize,
        LocalDateTime start,
        LocalDateTime end,
        Page<HistoryItemDto> historyPage
) {
}
