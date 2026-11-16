package org.example.balance.servicies.domain.currency;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.balance.entities.BalanceCurrency;
import org.example.balance.repositories.BalanceCurrencyRepository;
import org.example.exceptions.domain.client.NotFound;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor

public class FindCurrencyService {

    private final BalanceCurrencyRepository balanceCurrencyRepository;

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public BalanceCurrency findById(Long id) {
        return balanceCurrencyRepository.findById(id)
                .orElseThrow(() -> new NotFound("Валюта не найдена"));
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public List<BalanceCurrency> findAll() {
        return balanceCurrencyRepository.findAll();
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public BalanceCurrency findByIdForUpdate(Long id) {
        return balanceCurrencyRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new NotFound("Валюта не найдена"));
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public List<BalanceCurrency> findAllForUpdate() {
        return balanceCurrencyRepository.findAllForUpdate();
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public BalanceCurrency findByCurrency(BalanceCurrency currency) {
        return balanceCurrencyRepository.findByCurrency(currency)
                .orElseThrow(() -> new NotFound("Валюта не найдена"));
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public BalanceCurrency findByCurrencyForUpdate(BalanceCurrency currency) {
        return balanceCurrencyRepository.findByCurrencyForUpdate(currency)
                .orElseThrow(() -> new NotFound("Валюта не найдена"));
    }

}
