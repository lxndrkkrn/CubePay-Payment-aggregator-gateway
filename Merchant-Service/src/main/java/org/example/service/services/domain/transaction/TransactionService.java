package org.example.service.services.domain.transaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.domain.client.NotFound;
import org.example.service.entities.Store;
import org.example.service.entities.Transaction;
import org.example.service.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor

public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public Transaction createTransaction(
            String clientTransactionUuid,
            Store store,
            BigDecimal amount,
            String currency,
            String clientId,
            String clientIp,
            String callbackUrl,
            String redirectUrl
    ) {
        log.info("Создание новой транзакции: clientTransactionUuid: {}, clientId: {}, clientIp: {}, {} {}, StoreId: {}. {}", clientTransactionUuid, clientId, clientIp, amount, currency, store.getId(), LocalDateTime.now());

        Transaction transaction = new Transaction();

        transaction.setClientTransactionUuid(clientTransactionUuid);
        transaction.setStore(store);
        transaction.setAmount(amount);
        transaction.setCurrency(currency);
        transaction.setClientId(clientId);
        transaction.setClientIp(clientIp);
        transaction.setCallbackUrl(callbackUrl);
        transaction.setRedirectUrl(redirectUrl);

        transactionRepository.save(transaction);

        log.info("Транзакция {} ({}) создана в {}", transaction.getUuid(), transaction.getId(), transaction.getCreatedAt());
        return transaction;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void changeTransactionStatus(String uuid, String status) {
        log.info("Смена статуса на {} для транзакции {}", status, uuid);

        Transaction transaction = transactionRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFound("Транзакция не найденна"));

        transaction.setStatus(status);

        transactionRepository.save(transaction);

        log.info("Транзакция {} поменяла статус на {}", uuid, status);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void changeBankTransactionUuid(String uuid, String bankUuid) {
        log.info("Смена UUID банковской транзакции на {} для транзакции {}", bankUuid, uuid);

        Transaction transaction = transactionRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFound("Транзакция не найденна"));

        transaction.setBankTransactionUuid(bankUuid);

        transactionRepository.save(transaction);

        log.info("Транзакция {} поменяла UUID транзакции банка на {}", uuid, bankUuid);
    }

}
