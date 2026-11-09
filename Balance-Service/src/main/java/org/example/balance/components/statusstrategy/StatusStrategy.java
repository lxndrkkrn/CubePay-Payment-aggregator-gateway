package org.example.balance.components.statusstrategy;

import org.example.balance.entities.Balance;
import org.example.balance.enums.balance.BalanceOperation;
import org.example.balance.enums.balance.BalanceStatus;

public interface StatusStrategy {

    BalanceStatus getSupportedStatus();

    void validate(Balance balance, BalanceOperation operation);

}
