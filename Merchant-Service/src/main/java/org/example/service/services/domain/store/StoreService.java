package org.example.service.services.domain.store;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.domain.client.NotFound;
import org.example.service.entities.Store;
import org.example.service.repository.StoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor

public class StoreService {

    private final StoreRepository storeRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public Store createStore(String name, Long balanceId, Integer transactionsPerMinuteLimit, String secretKey) {
        log.info("Создание магазина '{}': balance Id {}, {} tran/min limit", name, balanceId, transactionsPerMinuteLimit);

        Store store = new Store();

        store.setBalanceId(balanceId);
        store.setTransactionsPerMinuteLimit(transactionsPerMinuteLimit);
        store.setSecretKey(secretKey);

        storeRepository.save(store);

        log.info("Магазин '{}' создан", name);
        return store;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void changeStatus(String uuid, String status) {
        log.info("Смена статуса на {} для магазина {}", status, uuid);

        Store store = storeRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFound("Магазин не найден"));

        store.setStatus(status);

        storeRepository.save(store);
        log.info("Новый статус для магазина {} установлен", uuid);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void changeTransactionsPerMinuteLimit(String uuid, Integer limit) {
        log.info("Смена лимита на {} тран/мин магазину {}", limit, uuid);

        Store store = storeRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFound("Магазин не найден"));

        store.setTransactionsPerMinuteLimit(limit);

        storeRepository.save(store);
        log.info("Новый лимит для магазина {} установлен", uuid);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void changeName(String uuid, String name) {
        log.info("Смена названия на {} для магазина {}", name, uuid);

        Store store = storeRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFound("Магазин не найден"));

        store.setName(name);

        storeRepository.save(store);
        log.info("Новое название для магазина {} установлено", uuid);
    }

}
