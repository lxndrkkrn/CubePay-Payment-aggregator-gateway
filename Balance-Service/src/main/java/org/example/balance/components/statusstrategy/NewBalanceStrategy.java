package org.example.balance.components.statusstrategy;

import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.domain.client.BadRequest;
import org.example.balance.entities.Balance;
import org.example.balance.enums.balance.BalanceOperation;
import org.example.balance.enums.balance.BalanceStatus;
import org.springframework.stereotype.Component;

@Component
@Slf4j

public class NewBalanceStrategy implements StatusStrategy {

    public BalanceStatus getSupportedStatus() {
        return BalanceStatus.NEW;
    }

    public void validate(Balance balance, BalanceOperation operation) {
        throw new BadRequest("Баланс еще не активирован (статус NEW). Операции запрещены.");
    }

}
