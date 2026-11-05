package org.example.service.services.domain.transaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.domain.client.NotFound;
import org.example.service.entities.Transaction;
import org.example.service.repository.TransactionRepository;
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
    public Transaction findTransactionByUuid(String uuid) {
        return transactionRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFound("Транзакция не найдена"));
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public List<Transaction> findAllTransactionByClientId(String id) {
        return transactionRepository.findAllByClientId(id);
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public List<Transaction> findAllTransactionByClientIp(String ip) {
        return transactionRepository.findAllByClientIp(ip);
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public List<Transaction> findAllTransactionByClientTransactionUuid(String clientTransactionUuid) {
        return transactionRepository.findAllByClientTransactionUuid(clientTransactionUuid);
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public List<Transaction> findAllTransactionByStatus(String status) {
        return transactionRepository.findAllByStatus(status);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Transaction findTransactionByUuidForUpdate(String uuid) {
        return transactionRepository.findByIdForUpdate(uuid)
                .orElseThrow(() -> new NotFound("Транзакция не найдена"));
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public Transaction findTransactionByBankTransactionUuid(String uuid) {
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
