package org.example.merchant.services.domain.transaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.merchant.repository.TransactionRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor

public class ValidateTransactionService {

    private final TransactionRepository transactionRepository;

    //

}
