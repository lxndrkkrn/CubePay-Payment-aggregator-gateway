package org.example.balance.components.balance;

import dto.balance.CompensateFailedRefundDto;
import dto.balance.ProcessPaymentRefundDto;
import dto.balance.ProcessPaymentSuccessDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.balance.servicies.application.balance.BalanceLifecycleAppService;
import org.example.balance.servicies.application.balance.PaymentSettlementAppService;
import org.example.balance.servicies.domain.balance.FindBalanceService;
import org.example.exceptions.domain.client.BadRequest;
import org.example.exceptions.domain.client.NotFound;
import org.example.exceptions.domain.client.PaymentRequired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor

public class PaymentSettlementListener {

    private final PaymentSettlementAppService paymentSettlementAppService;

    @KafkaListener(topics = "process-payment-success-topic", groupId = "balance-service")
    public void processPaymentSuccessHandle(ProcessPaymentSuccessDto event) {
        try {
            log.info("Получено сообщение об успехе платежа: {}", event != null ? event.transactionUuid() : null);
            paymentSettlementAppService.processPaymentSuccess(event);
        } catch (DataIntegrityViolationException e) {
            log.warn("Дубликат события успеха платежа. Игнорируем");
        } catch (BadRequest | NotFound e) {
            log.error("Невалидное сообщение успеха платежа: {}. Отброшено", e.getMessage());
        } catch (Exception e) {
            log.error("Временная ошибка при зачислении платежа. Будет повтор", e);
            throw e;
        }
    }

    @KafkaListener(topics = "process-payment-refund-topic", groupId = "balance-service")
    public void processPaymentRefundHandle(ProcessPaymentRefundDto event) {
        try {
            log.info("Получено сообщение о возврате: {}", event != null ? event.refundUuid() : null);
            paymentSettlementAppService.processPaymentRefund(event);
        } catch (DataIntegrityViolationException e) {
            log.warn("Дубликат события возврата. Игнорируем");
        } catch (BadRequest | NotFound | PaymentRequired e) {
            log.error("Бизнес-ошибка при возврате средств: {}. Отброшено", e.getMessage());
        } catch (Exception e) {
            log.error("Временная ошибка при возврате средств. Будет повтор", e);
            throw e;
        }
    }

    @KafkaListener(topics = "compensate-failed-refund-topic", groupId = "balance-service")
    public void compensateFailedRefundHandle(CompensateFailedRefundDto event) {
        try {
            log.info("Получено сообщение об откате неудачного возврата: {}", event != null ? event.refundUuid() : null);
            paymentSettlementAppService.compensateFailedRefund(event);
        } catch (DataIntegrityViolationException e) {
            log.warn("Дубликат события отката возврата. Игнорируем");
        } catch (BadRequest | NotFound e) {
            log.error("Невалидное сообщение отката возврата: {}. Отброшено", e.getMessage());
        } catch (Exception e) {
            log.error("Временная ошибка при откате возврата. Будет повтор", e);
            throw e;
        }
    }
}