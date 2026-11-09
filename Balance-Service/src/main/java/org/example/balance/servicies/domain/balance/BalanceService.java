package org.example.balance.servicies.domain.balance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.domain.client.BadRequest;
import org.example.exceptions.domain.client.NotFound;
import org.example.balance.entities.Balance;
import org.example.balance.enums.balance.BalanceStatus;
import org.example.balance.repositories.BalanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor

public class BalanceService {

    private final BalanceRepository balanceRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public Balance createBalance(Long storeId, Boolean isSystem) {
        log.info("Создание нового счёта (баланса): Id магазина: {}, {}", storeId, isSystem);

        if (isSystem == null) {
            throw new BadRequest("Флаг системного счёта (isSystem) не может быть null");
        }

        if (!isSystem && (storeId == null || storeId <= 0)) {
            throw new BadRequest("Для баланса магазина необходимо указать корректный storeId");
        }

        Balance balance = new Balance();

        balance.setStoreId(storeId);
        balance.setIsSystem(isSystem);

        balanceRepository.save(balance);

        log.info("Баланс для магазина {} ({}) сохранён", storeId, isSystem);
        return balance;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void changeStatus(BalanceStatus status, Long id) {
        log.info("Изменение статуса на {} для баланса {}", status, id);

        if (status == null || id == null || id <= 0) {
            throw new BadRequest("Не верные аргументы: Id магазина или статус не могут быть Null, а Id не может быть не положительным");
        }

        Balance balance = balanceRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new NotFound("Баланс не найден"));

        balance.setStatus(status);

        balanceRepository.save(balance);
        log.info("Статус баланса изменён");
    }

}
