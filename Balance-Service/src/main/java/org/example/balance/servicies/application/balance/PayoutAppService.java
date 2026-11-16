package org.example.balance.servicies.application.balance;

import dto.balance.CancelPayoutDto;
import dto.balance.ConfirmPayoutDto;
import dto.balance.RequestPayoutDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.balance.dtos.outbox.payoutappservice.CancelPayoutOutboxDto;
import org.example.balance.dtos.outbox.payoutappservice.ConfirmPayoutOutboxDto;
import org.example.balance.dtos.outbox.payoutappservice.PayoutFrozenOutboxDto;
import org.example.balance.entities.Balance;
import org.example.balance.servicies.domain.balance.ChangeBalanceService;
import org.example.balance.servicies.domain.balance.FindBalanceService;
import org.example.balance.servicies.domain.outbox.BalanceOutboxService;
import org.example.exceptions.domain.client.BadRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor

public class PayoutAppService {

    private final FindBalanceService findBalanceService;
    private final ChangeBalanceService changeBalanceService;

    private final BalanceOutboxService outboxService;

    @Transactional
    public void requestPayout(RequestPayoutDto event) {
        log.info("Получен запрос о выводе денег с баланса");

        validateRequestPayout(event);

        Balance storeBalance = findBalanceService.findByStoreId(event.storeId());

        String description = "Заморозка средств под заявку на вывод #" + event.transactionUuid();
        String idempotentKey = "PAYOUT_FREEZE_" + event.transactionUuid();

        changeBalanceService.releaseAvailableFunds(storeBalance.getId(), event.amount(), description, idempotentKey);

        PayoutFrozenOutboxDto outboxDto = new PayoutFrozenOutboxDto(
                event.transactionUuid(),
                event.storeId(),
                storeBalance.getId(),
                event.amount(),
                event.currency(),

                Instant.now()
        );

        outboxService.saveToOutbox("PAYOUT_FUNDS_FROZEN", outboxDto);
    }

    @Transactional
    public void confirmPayout(ConfirmPayoutDto event) {
        log.info("Получен ответ от банка об успехе операции вывода денег с баланса");

        validateConfirmPayout(event);

        Balance storeBalance = findBalanceService.findByStoreId(event.storeId());

        String description = "Списание замороженных средств # " + event.transactionUuid() + " ; (UUID транзакции запроса - " + event.requestTransactionUuid() + " )";
        String idempotentKey = "PAYOUT_CONFIRM_" + event.transactionUuid();

        changeBalanceService.takeHoldFunds(storeBalance.getId(), event.amount(), description, idempotentKey);

        ConfirmPayoutOutboxDto outboxDto = new ConfirmPayoutOutboxDto(
                event.transactionUuid(),
                event.requestTransactionUuid(),
                event.storeId(),
                storeBalance.getId(),
                event.amount(),
                event.currency(),

                Instant.now()
        );

        outboxService.saveToOutbox("PAYOUT_FUNDS_CANCEL", outboxDto);
    }

    @Transactional
    public void cancelPayout(CancelPayoutDto event) {
        log.info("Получен ответ от банка об отмене операции вывода денег с баланса. Причина: {}", event);

        validateCancelPayout(event);

        Balance storeBalance = findBalanceService.findByStoreId(event.storeId());

        String description = "Возврат замороженных средств # " + event.transactionUuid() + " ; (UUID транзакции запроса - " + event.requestTransactionUuid() + " )" + " Причина: " + event.cancelDescription();
        String idempotentKey = "PAYOUT_CANCEL_" + event.transactionUuid();

        changeBalanceService.releaseHoldFunds(storeBalance.getId(), event.amount(), description, idempotentKey);

        CancelPayoutOutboxDto outboxDto = new CancelPayoutOutboxDto(
                event.transactionUuid(),
                event.requestTransactionUuid(),
                event.storeId(),
                storeBalance.getId(),
                event.amount(),
                event.currency(),
                event.cancelDescription(),

                Instant.now()
        );

        outboxService.saveToOutbox("PAYOUT_FUNDS_CANCEL", outboxDto);
    }

    private void validateRequestPayout(RequestPayoutDto event) {
        if (event == null) {
            throw new BadRequest("Пустое событие");
        }
        if (event.storeId() == null || event.storeId() <= 0) {
            throw new BadRequest("Некоректный ID магазина");
        }
        if (event.amount() == null || event.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequest("Финансовые суммы не могут быть null или не положительные");
        }
        if (event.currency() == null || event.currency().isEmpty()) {
            throw new BadRequest("Валюта не может быть null");
        }
        if (event.transactionUuid() == null || event.transactionUuid().isEmpty()) {
            throw new BadRequest("UUID транзакции не может быть null");
        }
    }

    private void validateConfirmPayout(ConfirmPayoutDto event) {
        if (event == null) {
            throw new BadRequest("Пустое событие");
        }
        if (event.storeId() == null || event.storeId() <= 0) {
            throw new BadRequest("Некоректный ID магазина");
        }
        if (event.amount() == null || event.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequest("Финансовые суммы не могут быть null или не положительные");
        }
        if (event.currency() == null || event.currency().isEmpty()) {
            throw new BadRequest("Валюта не может быть null");
        }
        if (event.transactionUuid() == null || event.transactionUuid().isEmpty() || event.requestTransactionUuid() == null || event.requestTransactionUuid().isEmpty()) {
            throw new BadRequest("UUID транзакции не может быть null");
        }
    }

    private void validateCancelPayout(CancelPayoutDto event) {
        if (event == null) {
            throw new BadRequest("Пустое событие");
        }
        if (event.storeId() == null || event.storeId() <= 0) {
            throw new BadRequest("Некоректный ID магазина");
        }
        if (event.amount() == null || event.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequest("Финансовые суммы не могут быть null или не положительные");
        }
        if (event.currency() == null || event.currency().isEmpty()) {
            throw new BadRequest("Валюта не может быть null");
        }
        if (event.transactionUuid() == null || event.transactionUuid().isEmpty() || event.requestTransactionUuid() == null || event.requestTransactionUuid().isEmpty()) {
            throw new BadRequest("UUID транзакции не может быть null");
        }
    }

}
