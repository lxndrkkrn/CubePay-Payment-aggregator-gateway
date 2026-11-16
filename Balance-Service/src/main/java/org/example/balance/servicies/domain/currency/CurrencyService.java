package org.example.balance.servicies.domain.currency;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.balance.entities.BalanceCurrency;
import org.example.balance.enums.balance.Currency;
import org.example.balance.repositories.BalanceCurrencyRepository;
import org.example.exceptions.domain.client.BadRequest;
import org.example.exceptions.domain.client.NotFound;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor

public class CurrencyService {

    private final BalanceCurrencyRepository balanceCurrencyRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public BalanceCurrency createCurrency(Currency currency, BigDecimal multiply) {
        log.info("Создание новой валюты");

        if (multiply.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequest("Курс валюты должен быть положительным");
        }

        BalanceCurrency balanceCurrency = new BalanceCurrency();

        balanceCurrency.setCurrency(currency);
        balanceCurrency.setMultiply(multiply);

        balanceCurrencyRepository.save(balanceCurrency);

        log.info("Созданна новая валюта {} (Multiply: {})", currency, multiply);
        return balanceCurrency;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void deleteCurrencyById(Long id) {
        log.info("Удаление валюты");

        if (id == null || id <= 0) {
            throw new BadRequest("ID валюты должен быть положительным");
        }

        balanceCurrencyRepository.deleteById(id);

        log.info("Валюта удалена");
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void changeMultiply(Long id, BigDecimal multiply) {
        log.info("Изменение курса валюты");

        BalanceCurrency currency = balanceCurrencyRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new NotFound("Валюта не найдена"));

        currency.setMultiply(multiply);

        balanceCurrencyRepository.save(currency);
        log.info("Курс валюты изменён");
    }

}
