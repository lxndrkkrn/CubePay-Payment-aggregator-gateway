package org.example.balance.servicies.domain.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.balance.entities.outbox.BalanceOutbox;
import org.example.balance.repositories.outbox.BalanceOutboxRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

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

}
