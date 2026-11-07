package org.example.service.servicies.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.domain.client.NotFound;
import org.example.service.entities.Balance;
import org.example.service.repositories.BalanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor

public class ChangeBalanceService {

    private final BalanceRepository balanceRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public void addAvailableFunds(Long id, BigDecimal amount) {
        log.info("Блокировка баланса ID {} для зачисления суммы {}", id, amount);

        Balance balance = balanceRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new NotFound("Баланс не найден"));

        BigDecimal newBalance = balance.getAvailableBalance().add(amount);
        balance.setAvailableBalance(newBalance);

        balanceRepository.save(balance);
        log.info("Баланс успешно обновлен. Новый баланс: {}", newBalance);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void takeAvailableFunds(Long id, BigDecimal amount) {
        log.info("Блокировка баланса ID {} для вычитания суммы {}", id, amount);

        Balance balance = balanceRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new NotFound("Баланс не найден"));

        BigDecimal newBalance = balance.getAvailableBalance().subtract(amount);
        balance.setAvailableBalance(newBalance);

        balanceRepository.save(balance);
        log.info("Баланс успешно обновлен. Новый баланс: {}", newBalance);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void addHoldFunds(Long id, BigDecimal amount) {
        log.info("Блокировка баланса ID {} для добавления замороженной суммы {}", id, amount);

        Balance balance = balanceRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new NotFound("Баланс не найден"));

        BigDecimal newBalance = balance.getHoldBalance().add(amount);
        balance.setHoldBalance(newBalance);

        balanceRepository.save(balance);
        log.info("Баланс успешно обновлен. Новый замароженный баланс: {}", newBalance);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void takeHoldFunds(Long id, BigDecimal amount) {
        log.info("Блокировка баланса ID {} для вычитания замороженной суммы {}", id, amount);

        Balance balance = balanceRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new NotFound("Баланс не найден"));

        BigDecimal newBalance = balance.getHoldBalance().subtract(amount);
        balance.setHoldBalance(newBalance);

        balanceRepository.save(balance);
        log.info("Баланс успешно обновлен. Новый замароженный баланс: {}", newBalance);
    }

}
