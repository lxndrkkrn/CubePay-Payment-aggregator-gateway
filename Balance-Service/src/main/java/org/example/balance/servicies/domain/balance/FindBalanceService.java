package org.example.balance.servicies.domain.balance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.domain.client.NotFound;
import org.example.balance.entities.Balance;
import org.example.balance.enums.balance.BalanceStatus;
import org.example.balance.repositories.BalanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor

public class FindBalanceService {

    private final BalanceRepository balanceRepository;

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public Balance findByUuid(String uuid) {
        return balanceRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFound("Баланс не найден"));
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Balance findByIdForUpdate(String uuid) {
        return balanceRepository.findByUuidForUpdate(uuid)
                .orElseThrow(() -> new NotFound("Баланс не найден"));
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Balance findByUuidForUpdate(Long id) {
        return balanceRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new NotFound("Баланс не найден"));
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public Balance findByStoreId(Long id) {
        return balanceRepository.findByStoreId(id)
                .orElseThrow(() -> new NotFound("Баланс не найден"));
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Balance findByStoreIdForUpdate(Long id) {
        return balanceRepository.findByStoreIdForUpdate(id)
                .orElseThrow(() -> new NotFound("Баланс не найден"));
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public List<Balance> findByStatus(BalanceStatus status) {
        return balanceRepository.findByStatus(status);
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public Balance findFirstSystemBalance() {
        return balanceRepository.findFirstByIsSystemTrue()
                .orElseThrow(() -> new NotFound("Системный баланс не найден"));
    }

}
