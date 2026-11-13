package org.example.balance.servicies.domain.balance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.domain.client.BadRequest;
import org.example.exceptions.domain.client.NotFound;
import org.example.exceptions.domain.client.PaymentRequired;
import org.example.balance.entities.Balance;
import org.example.balance.enums.balance.BalanceOperation;
import org.example.balance.enums.history.HistoryBucketType;
import org.example.balance.enums.history.HistoryOperationType;
import org.example.balance.repositories.BalanceRepository;
import org.example.balance.servicies.domain.history.HistoryBalanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor

public class ChangeBalanceService {

    private final BalanceRepository balanceRepository;

    private final BalanceStatusValidator balanceStatusValidator;
    private final HistoryBalanceService historyBalanceService;

    private static final BigDecimal MAX_OPERATION_AMOUNT = new BigDecimal("50000000.00");
    private static final int MAX_SCALE = 4;

    @Transactional(propagation = Propagation.MANDATORY)
    public void addAvailableFunds(Long id, BigDecimal amount, String description, String idempotencyKey) {
        log.info("Блокировка баланса ID {} для зачисления суммы {}", id, amount);

        validateCommonInput(id, amount, idempotencyKey);

        Balance balance = balanceRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new NotFound("Баланс не найден"));

        balanceStatusValidator.validate(balance, BalanceOperation.ADD_AVAILABLE);

        BigDecimal balanceBefore = balance.getAvailableBalance();

        BigDecimal newBalance = balance.getAvailableBalance().add(amount);
        balance.setAvailableBalance(newBalance);

        balanceRepository.save(balance);
        historyBalanceService.createBalanceLog(id, HistoryOperationType.CREDIT, HistoryBucketType.AVAILABLE, amount, balanceBefore, newBalance, description, idempotencyKey);
        log.info("Баланс успешно обновлен. Новый баланс: {}", newBalance);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void takeAvailableFunds(Long id, BigDecimal amount, String description, String idempotencyKey) {
        log.info("Блокировка баланса ID {} для вычитания суммы {}", id, amount);

        validateCommonInput(id, amount, idempotencyKey);

        Balance balance = balanceRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new NotFound("Баланс не найден"));

        balanceStatusValidator.validate(balance, BalanceOperation.TAKE_AVAILABLE);

        if (balance.getAvailableBalance().compareTo(amount) < 0) {
            throw new PaymentRequired("Недостаточно баланса для списания");
        }

        BigDecimal balanceBefore = balance.getAvailableBalance();

        BigDecimal newBalance = balance.getAvailableBalance().subtract(amount);
        balance.setAvailableBalance(newBalance);

        balanceRepository.save(balance);
        historyBalanceService.createBalanceLog(id, HistoryOperationType.DEBIT, HistoryBucketType.AVAILABLE, amount, balanceBefore, newBalance, description, idempotencyKey);
        log.info("Баланс успешно обновлен. Новый баланс: {}", newBalance);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void addHoldFunds(Long id, BigDecimal amount, String description, String idempotencyKey) {
        log.info("Блокировка баланса ID {} для добавления замороженной суммы {}", id, amount);

        validateCommonInput(id, amount, idempotencyKey);

        Balance balance = balanceRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new NotFound("Баланс не найден"));

        if (Boolean.TRUE.equals(balance.getIsSystem())) {
            throw new BadRequest("Нельзя добавить замороженный баланс на системный счёт");
        }

        balanceStatusValidator.validate(balance, BalanceOperation.ADD_HOLD);

        BigDecimal balanceBefore = balance.getHoldBalance();

        BigDecimal newBalance = balance.getHoldBalance().add(amount);
        balance.setHoldBalance(newBalance);

        balanceRepository.save(balance);
        historyBalanceService.createBalanceLog(id, HistoryOperationType.CREDIT, HistoryBucketType.HOLD, amount, balanceBefore, newBalance, description, idempotencyKey);
        log.info("Баланс успешно обновлен. Новый замароженный баланс: {}", newBalance);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void takeHoldFunds(Long id, BigDecimal amount, String description, String idempotencyKey) {
        log.info("Блокировка баланса ID {} для вычитания замороженной суммы {}", id, amount);

        validateCommonInput(id, amount, idempotencyKey);

        Balance balance = balanceRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new NotFound("Баланс не найден"));

        if (Boolean.TRUE.equals(balance.getIsSystem())) {
            throw new BadRequest("Нельзя списывать замороженный баланс с системного счёта");
        }

        balanceStatusValidator.validate(balance, BalanceOperation.TAKE_HOLD);

        if (balance.getHoldBalance().compareTo(amount) < 0) {
            throw new PaymentRequired("Недостаточно замороженного баланса для списания");
        }

        BigDecimal balanceBefore = balance.getHoldBalance();

        BigDecimal newBalance = balance.getHoldBalance().subtract(amount);
        balance.setHoldBalance(newBalance);

        balanceRepository.save(balance);
        historyBalanceService.createBalanceLog(id, HistoryOperationType.DEBIT, HistoryBucketType.HOLD, amount, balanceBefore, newBalance, description, idempotencyKey);
        log.info("Баланс успешно обновлен. Новый замароженный баланс: {}", newBalance);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void releaseHoldFunds(Long id, BigDecimal amount, String description, String idempotencyKey) {
        log.info("Размораживание денег ({}) у баланса {}", amount, id);

        validateCommonInput(id, amount, idempotencyKey);

        Balance balance = balanceRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new NotFound("Баланс не найден"));

        if (Boolean.TRUE.equals(balance.getIsSystem())) {
            throw new BadRequest("У системного счёта не может быть замороженных средств");
        }

        balanceStatusValidator.validate(balance, BalanceOperation.TAKE_HOLD);
        balanceStatusValidator.validate(balance, BalanceOperation.ADD_AVAILABLE);

        BigDecimal holdBalanceBefore = balance.getHoldBalance();
        BigDecimal availableBalanceBefore = balance.getAvailableBalance();

        if (holdBalanceBefore.compareTo(amount) < 0) {
            throw new PaymentRequired("Недостаточно замороженного баланса для разморозки");
        }

        BigDecimal newHoldBalance = holdBalanceBefore.subtract(amount);
        BigDecimal newAvailableBalance = availableBalanceBefore.add(amount);

        balance.setHoldBalance(newHoldBalance);
        balance.setAvailableBalance(newAvailableBalance);

        balanceRepository.save(balance);

        historyBalanceService.createBalanceLog(
                id, HistoryOperationType.DEBIT, HistoryBucketType.HOLD,
                amount, holdBalanceBefore, newHoldBalance, description, idempotencyKey + "_DEBIT_HOLD"
        );
        historyBalanceService.createBalanceLog(
                id, HistoryOperationType.CREDIT, HistoryBucketType.AVAILABLE,
                amount, availableBalanceBefore, newAvailableBalance, description, idempotencyKey + "_CREDIT_AVAILABLE"
        );

        log.info("Баланс обновлен: деньги разморожены. Hold balance: {}; Available balance: {}", newHoldBalance, newAvailableBalance);
    }

    private void validateCommonInput(Long id, BigDecimal amount, String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new BadRequest("idempotencyKey не должен быть пустым");
        }
        if (id == null || id <= 0) {
            throw new BadRequest("Некорректный идентификатор баланса");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequest("Сумма операции должна быть строго больше нуля");
        }
        if (amount.stripTrailingZeros().scale() > MAX_SCALE) {
            throw new BadRequest("Превышена допустимая точность суммы (максимум 4 знака после запятой)");
        }
        if (amount.compareTo(MAX_OPERATION_AMOUNT) > 0) {
            throw new BadRequest("Сумма операции превышает системный лимит безопасности");
        }
    }
}
