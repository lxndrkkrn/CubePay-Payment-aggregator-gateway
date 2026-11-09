package org.example.merchant.services.domain.store;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.domain.client.NotFound;
import org.example.merchant.entities.Store;
import org.example.merchant.repository.StoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FindStoreService {

    private final StoreRepository storeRepository;

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public Store findByUuid(String uuid) {
        return storeRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFound("Магазин с UUID " + uuid + " не найден"));
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public Store findBySecretKey(String secretKey) {
        return storeRepository.findBySecretKey(secretKey)
                .orElseThrow(() -> new NotFound("Магазин с таким секретным ключом не найден"));
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public Store findByBalanceId(Long balanceId) {
        return storeRepository.findByBalanceId(balanceId)
                .orElseThrow(() -> new NotFound("Магазин для баланса " + balanceId + " не найден"));
    }
}
