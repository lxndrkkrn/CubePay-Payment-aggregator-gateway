package org.example.balance.components.statusstrategy;

import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.domain.client.BadRequest;
import org.example.balance.entities.Balance;
import org.example.balance.enums.balance.BalanceOperation;
import org.example.balance.enums.balance.BalanceStatus;
import org.springframework.stereotype.Component;

@Component
@Slf4j

public class FrozenOutgoingBalanceStrategy implements StatusStrategy {

    public BalanceStatus getSupportedStatus() {
        return BalanceStatus.FROZEN_OUTGOING;
    }

    public void validate(Balance balance, BalanceOperation operation) {
        if (operation == BalanceOperation.TAKE_AVAILABLE) {
            throw new BadRequest("Вывод средств временно заморожен службой безопасности.");
        }
    }

}
