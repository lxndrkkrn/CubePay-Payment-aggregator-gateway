package org.example.balance.servicies.application.balance;

import dto.balance.CompensateFailedRefundDto;
import dto.balance.ProcessPaymentRefundDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import dto.balance.ProcessPaymentSuccessDto;
import org.example.balance.dtos.outbox.balancesettlement.PaymentRefundOutboxDto;
import org.example.balance.dtos.outbox.balancesettlement.PaymentSuccessOutboxDto;
import org.example.balance.dtos.outbox.balancesettlement.ReturnRefundOutboxDto;
import org.example.balance.entities.Balance;
import org.example.balance.entities.outbox.BalanceOutbox;
import org.example.balance.servicies.domain.balance.BalanceService;
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

public class PaymentSettlementAppService {

    private final FindBalanceService findBalanceService;
    private final ChangeBalanceService changeBalanceService;

    private final BalanceOutboxService outboxService;

    @Transactional
    public void processPaymentSuccess(ProcessPaymentSuccessDto event) {
        log.info("Получен ответ у банка об успехе операции");

        validateSuccessEvent(event);

        Balance storeBalance = findBalanceService.findByStoreId(event.storeId());
        Balance systemBalance = findBalanceService.findFirstSystemBalance();

        if (event.storeAmount().compareTo(BigDecimal.ZERO) > 0) {

            String idempotentKey = "SETTLE_STORE_" + event.transactionUuid();
            String description = "Зачисление по платежу #" + event.transactionUuid();

            if (event.isHoldRequired()) {
                changeBalanceService.addHoldFunds(storeBalance.getId(), event.storeAmount(), description, idempotentKey);
            } else {
                changeBalanceService.addAvailableFunds(storeBalance.getId(), event.storeAmount(), description, idempotentKey);
            }
        }

        if (event.gatewayFee().compareTo(BigDecimal.ZERO) > 0) {

            String feeIdempotentKey = "SETTLE_FEE_" + event.transactionUuid();
            String description = "Комиссия агрегатора по платежу #" + event.transactionUuid();

            changeBalanceService.addAvailableFunds(systemBalance.getId(), event.gatewayFee(), description, feeIdempotentKey);
        }

        PaymentSuccessOutboxDto dto = new PaymentSuccessOutboxDto(
                event.transactionUuid(),
                event.storeId(),
                Instant.now()
        );

        outboxService.saveToOutbox("PROCESS_PAYMENT_SUCCESS", dto);
    }

    @Transactional
    public void processPaymentRefund(ProcessPaymentRefundDto event) {
        log.info("Обработка отмены платежа и возврат баланса");

        validateRefundEvent(event);

        Balance storeBalance = findBalanceService.findByStoreId(event.storeId());
        Balance systemBalance = findBalanceService.findFirstSystemBalance();

        if (event.storeDebitAmount().compareTo(BigDecimal.ZERO) > 0) {

            String idempotentKey = "REFUND_STORE_" + event.refundUuid();
            String description = "Возврат по платежу #" + event.originalTransactionUuid();

            if (event.fromHoldBalance()) {
                changeBalanceService.takeHoldFunds(storeBalance.getId(), event.storeDebitAmount(), description, idempotentKey);
            } else {
                changeBalanceService.takeAvailableFunds(storeBalance.getId(), event.storeDebitAmount(), description, idempotentKey);
            }
        }

        if (event.feeRefundAmount().compareTo(BigDecimal.ZERO) > 0) {

            String idempotentKey = "REFUND_FEE_" + event.refundUuid();
            String description = "Возврат комиссии по платежу #" + event.originalTransactionUuid();

            changeBalanceService.takeAvailableFunds(systemBalance.getId(), event.feeRefundAmount(), description, idempotentKey);
        }

        PaymentRefundOutboxDto dto = new PaymentRefundOutboxDto(
                event.refundUuid(),
                event.originalTransactionUuid(),
                event.storeId(),
                Instant.now()
        );

        outboxService.saveToOutbox("PROCESS_PAYMENT_REFUND", dto);
    }

    @Transactional
    public void compensateFailedRefund(CompensateFailedRefundDto event) {
        log.info("Банк вернул ошибку при обработке платежа. Откатываем...");

        validateCompensateFailedRefundEvent(event);

        Balance storeBalance = findBalanceService.findByStoreId(event.storeId());
        Balance systemBalance = findBalanceService.findFirstSystemBalance();

        if (event.storeReturnAmount().compareTo(BigDecimal.ZERO) > 0) {

            String idempotentKey = "RETURN_STORE_" + event.refundUuid();
            String description = "Отмена возврата платежа #" + event.originalTransactionUuid() + " (Причина: " + event.failureReason() + ")";

            if (event.toHoldBalance()) {
                changeBalanceService.addHoldFunds(storeBalance.getId(), event.storeReturnAmount(), description, idempotentKey);
            } else {
                changeBalanceService.addAvailableFunds(storeBalance.getId(), event.storeReturnAmount(), description, idempotentKey);
            }
        }

        if (event.feeReturnAmount().compareTo(BigDecimal.ZERO) > 0) {

            String idempotentKey = "RETURN_FEE_" + event.refundUuid();

            String description = "Отмена комиссии возврата платежа #" + event.originalTransactionUuid() + " (Причина: " + event.failureReason() + ")";

            if (event.failureReason() == null || event.failureReason().isEmpty()) {
                description = "Отмена комиссии возврата платежа #" + event.originalTransactionUuid() + " (Причина неизвестна)";
            }

            changeBalanceService.addAvailableFunds(systemBalance.getId(), event.feeReturnAmount(), description, idempotentKey);
        }

        ReturnRefundOutboxDto dto = new ReturnRefundOutboxDto(
                event.refundUuid(),
                event.originalTransactionUuid(),
                event.storeId(),
                Instant.now()
        );

        outboxService.saveToOutbox("PROCESS_RETURN_REFUND", dto);
    }

    private void validateSuccessEvent(ProcessPaymentSuccessDto event) {
        if (event == null || event.transactionUuid() == null || event.transactionUuid().isBlank()) {
            throw new BadRequest("Отсутствует UUID транзакции");
        }
        if (event.storeId() == null || event.storeId() <= 0) {
            throw new BadRequest("Некорректный ID магазина");
        }
        if (event.totalAmount() == null || event.storeAmount() == null || event.gatewayFee() == null) {
            throw new BadRequest("Финансовые суммы не могут быть null");
        }
        if (event.storeAmount().compareTo(BigDecimal.ZERO) < 0 || event.gatewayFee().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequest("Доля магазина и комиссия не могут быть отрицательными");
        }

        BigDecimal calculatedTotal = event.storeAmount().add(event.gatewayFee());

        if (calculatedTotal.compareTo(event.totalAmount()) != 0) {
            log.error("Ошибка сходимости сумм по tx {}: storeAmount({}) + gatewayFee({}) != totalAmount({})",
                    event.transactionUuid(), event.storeAmount(), event.gatewayFee(), event.totalAmount());
            throw new BadRequest("Нарушен баланс сходимости платежа: сумма долей не равна общей сумме");
        }
    }

    private void validateRefundEvent(ProcessPaymentRefundDto event) {
        if (event == null || event.refundUuid() == null || event.refundUuid().isBlank()) {
            throw new BadRequest("Отсутствует UUID транзакции");
        }
        if (event.originalTransactionUuid() == null || event.originalTransactionUuid().isBlank()) {
            throw new BadRequest("Отсутствует UUID транзакции");
        }
        if (event.storeId() == null || event.storeId() <= 0) {
            throw new BadRequest("Некорректный ID магазина");
        }
        if (event.refundTotalAmount() == null || event.storeDebitAmount() == null || event.feeRefundAmount() == null) {
            throw new BadRequest("Финансовые суммы не могут быть null");
        }
        if (event.storeDebitAmount().compareTo(BigDecimal.ZERO) < 0 || event.feeRefundAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequest("Доля магазина и комиссия не могут быть отрицательными");
        }
    }

    private void validateCompensateFailedRefundEvent(CompensateFailedRefundDto event) {
        if (event == null || event.refundUuid() == null || event.refundUuid().isBlank()) {
            throw new BadRequest("Отсутствует UUID транзакции");
        }
        if (event.originalTransactionUuid() == null || event.originalTransactionUuid().isBlank()) {
            throw new BadRequest("Отсутствует UUID транзакции");
        }
        if (event.storeId() == null || event.storeId() <= 0) {
            throw new BadRequest("Некорректный ID магазина");
        }
        if (event.storeReturnAmount() == null || event.feeReturnAmount() == null) {
            throw new BadRequest("Финансовые суммы не могут быть null");
        }
        if (event.storeReturnAmount().compareTo(BigDecimal.ZERO) < 0 || event.feeReturnAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequest("Доля магазина и комиссия не могут быть отрицательными");
        }
        if (event.storeReturnAmount().compareTo(BigDecimal.ZERO) > 0 && event.feeReturnAmount().compareTo(BigDecimal.ZERO) > 0) {
            throw new BadRequest("Финансовые суммы должны быть строго больше нуля");
        }
    }

}
