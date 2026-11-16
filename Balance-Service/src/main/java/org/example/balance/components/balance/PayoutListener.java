package org.example.balance.components.balance;

import dto.balance.CancelPayoutDto;
import dto.balance.ConfirmPayoutDto;
import dto.balance.ProcessPaymentSuccessDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.balance.servicies.application.balance.PayoutAppService;
import org.example.exceptions.domain.client.BadRequest;
import org.example.exceptions.domain.client.NotFound;
import org.example.exceptions.domain.client.PaymentRequired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor

public class PayoutListener {

    private final PayoutAppService payoutAppService;

    @KafkaListener(topics = "confirm-payout-topic", groupId = "balance-service")
    public void confirmPayoutListener(ConfirmPayoutDto event) {
        try {
            log.info("Получено сообщение об успехе вывода дленег: {}", event != null ? event.transactionUuid() : null);
            payoutAppService.confirmPayout(event);
        } catch (DataIntegrityViolationException e) {
            log.warn("Дубликат события успеха платежа. Игнорируем");
        } catch (BadRequest | NotFound | PaymentRequired e) {
            log.error("Невалидное сообщение успеха вывода: {}. Отброшено", e.getMessage());
        } catch (Exception e) {
            log.error("Временная ошибка при обработке успеха. Будет повтор", e);
            throw e;
        }
    }

    @KafkaListener(topics = "cancel-payout-topic", groupId = "balance-service")
    public void cancelPayoutListener(CancelPayoutDto event) {
        try {
            log.info("Получено сообщение об ошибке вывода дленег: {}", event != null ? event.transactionUuid() : null);
            payoutAppService.cancelPayout(event);
        } catch (DataIntegrityViolationException e) {
            log.warn("Дубликат события ошибки платежа. Игнорируем");
        } catch (BadRequest | NotFound | PaymentRequired e) {
            log.error("Невалидное сообщение ошибки вывода: {}. Отброшено", e.getMessage());
        } catch (Exception e) {
            log.error("Временная ошибка при обработке ошибки платежа. Будет повтор", e);
            throw e;
        }
    }
}
