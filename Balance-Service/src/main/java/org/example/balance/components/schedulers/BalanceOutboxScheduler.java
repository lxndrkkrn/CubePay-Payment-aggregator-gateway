package org.example.balance.components.schedulers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.balance.entities.outbox.BalanceOutbox;
import org.example.balance.servicies.domain.outbox.BalanceOutboxService;
import org.example.balance.servicies.domain.outbox.FindBalanceOutboxService;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor

public class BalanceOutboxScheduler  {

    private final BalanceOutboxService balanceOutboxService;
    private final FindBalanceOutboxService findBalanceOutboxService;

    private final KafkaTemplate<String, String> stringKafkaTemplate;

    private final Map<String, String> EVENT_MAP = Map.of(
            "CREATE_STORE_BALANCE", "store-balance-created-topic",
            "CREATE_SYSTEM_BALANCE", "system-balance-created-topic",
            "BALANCE_STATUS_CHANGED", "balance-status-changed-topic",
            "PROCESS_PAYMENT_SUCCESS", "payment-settled-topic",
            "PROCESS_PAYMENT_REFUND", "refund-debited-topic",
            "PROCESS_RETURN_REFUND", "refund-compensated-topic",
            "PAYOUT_FUNDS_FROZEN", "payout-frozen-topic"
    );

    @Scheduled(fixedDelay = 500)
    @Transactional
    public void relayOutboxEvents() {
        List<BalanceOutbox> outboxList = findBalanceOutboxService.findNewEventsBatchForUpdate(PageRequest.of(0, 50), "NEW");

        if (outboxList == null || outboxList.isEmpty()) {
            return;
        }

        for (BalanceOutbox outbox : outboxList) {

            String topic = EVENT_MAP.get(outbox.getEventType());

            if (topic == null) {
                log.error("Неизвестный тип события Outbox ID {}: {}", outbox.getId(), outbox.getEventType());
                outbox.setStatus("FAILED");
                continue;
            }

            try {
                stringKafkaTemplate.send(topic, outbox.getIdempotentKey(), outbox.getPayload()).get(5, TimeUnit.SECONDS);

                outbox.setStatus("PROCESSED");

            } catch (Exception e) {
                log.error("Ошибка отправки Outbox ID {} в топик {}. Повторим на следующем тике.", outbox.getId(), topic, e);
            }
        }

        balanceOutboxService.saveAllToOutbox(outboxList);
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void removeOldProcessedEvents() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        int deletedCount = balanceOutboxService.deleteOldProcessedEvents(sevenDaysAgo);

        if (deletedCount > 0) {
            log.info("Очистка Outbox: удалено {} старых обработанных записей", deletedCount);
        }
    }
}
