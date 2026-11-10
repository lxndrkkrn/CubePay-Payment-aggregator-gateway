package org.example.balance.servicies.application.balance;

import dto.balance.ChangeBalanceStatusDto;
import dto.balance.CreateStoreBalanceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.balance.dtos.outbox.ChangeBalanceStatusOutboxDto;
import org.example.balance.dtos.outbox.CreateStoreBalanceOutboxDto;
import org.example.balance.dtos.outbox.CreateSystemBalanceOutboxDto;
import org.example.balance.entities.Balance;
import org.example.balance.enums.balance.BalanceStatus;
import org.example.balance.servicies.domain.balance.BalanceService;
import org.example.balance.servicies.domain.balance.BalanceStatusValidator;
import org.example.balance.servicies.domain.outbox.BalanceOutboxService;
import org.example.exceptions.domain.client.BadRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
@RequiredArgsConstructor

public class BalanceLifecycleAppService {

    private final BalanceService balanceService;

    private final BalanceOutboxService outboxService;

    @Transactional
    public void createStoreBalance(CreateStoreBalanceDto event) {
        log.info("Создание и привязка нового магазина к счёту");

        if (event == null || event.storeId() == null) {
            throw new BadRequest("Содержимое события повреждено или неверно");
        }

        Balance balance = balanceService.createBalance(event.storeId(), false);

        CreateStoreBalanceOutboxDto dto = new CreateStoreBalanceOutboxDto(
                balance.getId(),
                event.storeId()
        );

        outboxService.saveToOutbox("CREATE_STORE_BALANCE", dto);
    }

    @Transactional
    public void createSystemBalance() {
        log.info("Создание системного счёта");

        Balance systemBalance = balanceService.createBalance(null, true);

        CreateSystemBalanceOutboxDto dto = new CreateSystemBalanceOutboxDto(
                systemBalance.getId()
        );

        outboxService.saveToOutbox("CREATE_SYSTEM_BALANCE", dto);
    }

    @Transactional
    public void changeBalanceStatus(ChangeBalanceStatusDto event) {
        log.info("Изменение статуса баланса");

        if (event == null || event.id() == null || event.status() == null || event.status().isBlank()) {
            throw new BadRequest("Содержимое события повреждено или неверно");
        }

        try {
            BalanceStatus status = BalanceStatus.valueOf(event.status().toUpperCase());

            balanceService.changeStatus(status, event.id());

            ChangeBalanceStatusOutboxDto dto = new ChangeBalanceStatusOutboxDto(
                    event.id(),
                    status.name()
            );

            outboxService.saveToOutbox("CHANGED_BALANCE_STATUS", dto);

        } catch (IllegalArgumentException e) {
            log.error("Критическая ошибка при изменении статуса баланса на {}", event.status(), e);
            throw new RuntimeException("Неизвестный статус для балансов", e);
        }
    }

//    @Transactional(propagation = Propagation.MANDATORY)
//    public void saveToOutbox(String eventType, )

}
