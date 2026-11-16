package org.example.balance.components.balance;

import dto.balance.CreateStoreBalanceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.balance.dtos.outbox.balancelifecycle.CreateSystemBalanceOutboxDto;
import org.example.balance.servicies.application.balance.BalanceLifecycleAppService;
import org.example.balance.servicies.domain.balance.FindBalanceService;
import org.example.exceptions.domain.client.BadRequest;
import org.example.exceptions.domain.client.NotFound;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.rmi.ServerError;

@Component
@Slf4j
@RequiredArgsConstructor

public class BalanceLifecycleListener {

    private final BalanceLifecycleAppService balanceLifecycleAppService;

    @KafkaListener(topics = "create-store-balance-topic", groupId = "balance-service")
    public void createStoreBalanceHandle(CreateStoreBalanceDto event) {
        try {
            log.info("Получено сообщение из брокера о создании баланса");
            balanceLifecycleAppService.createStoreBalance(event);
        } catch (DataIntegrityViolationException e) {
            log.warn("Получен дубликат сообщения. Игнорируем");
        } catch (BadRequest | NotFound e) {
            log.error("Невалидное сообщение: {}. Сообщение отброшено", e.getMessage());
        } catch (Exception e) {
            log.error("Временная ошибка при чтении сообщения. Будет выполнен повтор", e);
            throw e;
        }
    }

}
