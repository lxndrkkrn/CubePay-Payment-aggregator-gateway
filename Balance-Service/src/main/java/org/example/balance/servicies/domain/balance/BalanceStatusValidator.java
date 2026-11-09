package org.example.balance.servicies.domain.balance;

import lombok.extern.slf4j.Slf4j;
import org.example.balance.components.statusstrategy.StatusStrategy;
import org.example.balance.entities.Balance;
import org.example.balance.enums.balance.BalanceOperation;
import org.example.balance.enums.balance.BalanceStatus;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j

public class BalanceStatusValidator {

    private final Map<BalanceStatus, StatusStrategy> strategies;

    public BalanceStatusValidator(List<StatusStrategy> strategyList) {
        this.strategies = new EnumMap<>(BalanceStatus.class);
        for (StatusStrategy strategy : strategyList) {
            this.strategies.put(strategy.getSupportedStatus(), strategy);
        }
    }

    public void validate(Balance balance, BalanceOperation operation) {
        StatusStrategy strategy = strategies.get(balance.getStatus());

        if (strategy == null) {
            throw new IllegalStateException("Не найдена стратегия валидации для статуса: " + balance.getStatus());
        }

        strategy.validate(balance, operation);
    }
}
