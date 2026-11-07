package org.example.service.servicies.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.domain.client.NotFound;
import org.example.service.entities.Balance;
import org.example.service.repositories.BalanceRepository;
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

        Balance balance = new Balance();

        balance.setStoreId(storeId);
        balance.setIsSystem(isSystem);

        balanceRepository.save(balance);

        log.info("Баланс для магазина {} ({}) сохранён", storeId, isSystem);
        return balance;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void changeStatus(String status, Long id) {
        log.info("Изменение статуса на {} для баланса {}", status, id);

        Balance balance = balanceRepository.findById(id)
                .orElseThrow(() -> new NotFound("Баланс не найден"));

        balance.setStatus(status);

        balanceRepository.save(balance);
        log.info("Статус баланса изменён");
    }

}
