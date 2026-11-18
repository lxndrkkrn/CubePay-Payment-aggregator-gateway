package org.example.balance.servicies.domain.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.balance.entities.outbox.BalanceOutbox;
import org.example.balance.repositories.outbox.BalanceOutboxRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor

public class BalanceOutboxService {

    private final BalanceOutboxRepository balanceOutboxRepository;

    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public void saveToOutbox(String eventType, Object payload) {
        try {
            BalanceOutbox outbox = new BalanceOutbox();

            outbox.setEventType(eventType);
            outbox.setPayload(objectMapper.writeValueAsString(payload));

            balanceOutboxRepository.save(outbox);
            log.info("Событие {} поставленно в очередь Outbox", outbox.getId());

        } catch (Exception e) {
            log.error("Критическая ошибка при сохранении события {} в Outbox", eventType, e);
            throw new RuntimeException("Не удалось сохранить событие в Outbox", e);
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void saveAllToOutbox(List<BalanceOutbox> outbox) {
        try {
            balanceOutboxRepository.saveAll(outbox);
        } catch (Exception e) {
            log.error("Критическая ошибка при сохранении записей {} в Outbox", outbox, e);
            throw new RuntimeException("Не удалось сохранить записи в Outbox", e);
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void deleteAllFromOutbox(List<BalanceOutbox> outbox) {
        try {
            balanceOutboxRepository.deleteAll(outbox);
        } catch (Exception e) {
            log.error("Критическая ошибка при удалении записей {} в Outbox", outbox, e);
            throw new RuntimeException("Не удалось удалить записи в Outbox", e);
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public int deleteOldProcessedEvents(LocalDateTime time) {
        try {
            return balanceOutboxRepository.deleteOldProcessedEvents(time);
        } catch (Exception e) {
            log.error("Критическая ошибка при удалении записей {} в Outbox", time, e);
            throw new RuntimeException("Не удалось удалить записи в Outbox", e);
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void saveToOutboxAndFlush(BalanceOutbox outbox) {
        try {
            balanceOutboxRepository.saveAndFlush(outbox);
        } catch (Exception e) {
            log.error("Критическая ошибка при моментальном сохранении записи {} outbox", outbox, e);
            throw new RuntimeException("Не удалось моментально сохранить запись outbox", e);
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void saveAllToOutboxAndFlush(List<BalanceOutbox> outbox) {
        try {
            balanceOutboxRepository.saveAllAndFlush(outbox);
        } catch (Exception e) {
            log.error("Критическая ошибка при моментальном сохранении записей {} outbox", outbox, e);
            throw new RuntimeException("Не удалось моментально сохранить записи outbox", e);
        }
    }

}
