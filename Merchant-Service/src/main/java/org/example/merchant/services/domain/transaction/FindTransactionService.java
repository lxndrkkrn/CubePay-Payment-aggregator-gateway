package org.example.merchant.services.domain.transaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.domain.client.NotFound;
import org.example.merchant.entities.Transaction;
import org.example.merchant.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor

public class FindTransactionService {

    private final TransactionRepository transactionRepository;

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public Transaction findByUuid(String uuid) {
        return transactionRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFound("Транзакция не найдена"));
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public List<Transaction> findAllByClientId(String id) {
        return transactionRepository.findAllByClientId(id);
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public List<Transaction> findAllByClientIp(String ip) {
        return transactionRepository.findAllByClientIp(ip);
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public List<Transaction> findAllByClientTransactionUuid(String clientTransactionUuid) {
        return transactionRepository.findAllByClientTransactionUuid(clientTransactionUuid);
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public List<Transaction> findAllByStatus(String status) {
        return transactionRepository.findAllByStatus(status);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Transaction findByUuidForUpdate(String uuid) {
        return transactionRepository.findByIdForUpdate(uuid)
                .orElseThrow(() -> new NotFound("Транзакция не найдена"));
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public Transaction findByBankTransactionUuid(String uuid) {
        return transactionRepository.findByBankTransactionUuid(uuid)
                .orElseThrow(() -> new NotFound("Транзакция не найдена"));
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public long countRecentTransactionsByIp(String ip, String status, int minutesAgo) {
        return transactionRepository.countByStatusAndIpInTimeWindow(status, ip, LocalDateTime.now().minusMinutes(minutesAgo));
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public long countRecentTransactionsByClientId(String clientId, String status, int minutesAgo) {
        return transactionRepository.countByStatusAndClientIdInTimeWindow(status, clientId, LocalDateTime.now().minusMinutes(minutesAgo));
    }


}
