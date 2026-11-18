package org.example.balance.components.currencyratesync;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.balance.entities.BalanceCurrency;
import org.example.balance.enums.balance.Currency;
import org.example.balance.repositories.BalanceCurrencyRepository;
import org.example.balance.servicies.domain.currency.CurrencyService;
import org.example.balance.servicies.domain.currency.FindCurrencyService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor

public class CbrCurrencyRateSyncScheduler {
    private final BalanceCurrencyRepository balanceCurrencyRepository;

    private final CurrencyService currencyService;
    private final FindCurrencyService findCurrencyService;

    private final CbrCurrencyRateProvider cbrCurrencyRateProvider;

    @Scheduled(initialDelay = 2000, fixedRate = 3600000)
    @Transactional
    public void currencySyncHandle() {
        log.info("Шлём Webhook в ЦБ и синхронизируем множетели курсов валют...");

        List<BalanceCurrency> balanceCurrencyList = findCurrencyService.findAllForUpdate();
        if (balanceCurrencyList.isEmpty()) {
            log.warn("В БД нет ни одной валюты");
            return;
        }

        Set<String> supportedCodes = Arrays.stream(Currency.values())
                .map(Enum::name)
                .collect(Collectors.toSet());

        Map<String, BigDecimal> freshRates = cbrCurrencyRateProvider.fetchCurrencyMultipliers(supportedCodes);

        if (freshRates.isEmpty()) {
            log.warn("Ни одной валюты по их коду полученно не было");
            return;
        }

        for (Map.Entry<String, BigDecimal> entry : freshRates.entrySet()) {
            Currency currencyEnum = Currency.valueOf(entry.getKey());
            BigDecimal newMultiplier = entry.getValue();

            BalanceCurrency entity = balanceCurrencyRepository.findByCurrency(currencyEnum)
                    .orElseGet(() -> {
                        BalanceCurrency newEntity = new BalanceCurrency();
                        newEntity.setCurrency(currencyEnum);
                        return newEntity;
                    });

            entity.setMultiply(newMultiplier);
            balanceCurrencyRepository.save(entity);
        }
    }

}
