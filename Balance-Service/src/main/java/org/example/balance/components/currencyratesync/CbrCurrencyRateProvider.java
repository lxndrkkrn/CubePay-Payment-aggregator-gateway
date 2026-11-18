package org.example.balance.components.currencyratesync;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.balance.dtos.cbr.CbrDailyResponse;
import org.example.balance.dtos.cbr.CbrValuteDto;
import org.example.balance.entities.BalanceCurrency;
import org.example.balance.servicies.domain.currency.CurrencyService;
import org.example.balance.servicies.domain.currency.FindCurrencyService;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Component
@Slf4j
//@RequiredArgsConstructor

public class CbrCurrencyRateProvider {

    private static final String CBR_DAILY_URL = "https://cbr-xml-daily.ru";

    private final RestClient restClient;

    public CbrCurrencyRateProvider() {
        this.restClient = RestClient.create();
    }

    public Map<String, BigDecimal> fetchCurrencyMultipliers(Set<String> targetCurrency) {
        try {
            CbrDailyResponse response = restClient.get().uri(CBR_DAILY_URL).retrieve().body(CbrDailyResponse.class);

            if (response == null || response.valute() == null) {
                log.warn("Пустой ответ от API курсов валют");
                return Collections.emptyMap();
            }

            Map<String, BigDecimal> multiplies = new HashMap<>();
            multiplies.put("RUB", BigDecimal.ONE);

            for (String currencyCode  : targetCurrency) {
                if ("RUB".equalsIgnoreCase(currencyCode)) {
                    continue;
                }

                CbrValuteDto valute = response.valute().get(currencyCode.toUpperCase());

                if (valute != null && valute.value() != null && valute.value().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal nominal = BigDecimal.valueOf(valute.nominal());
                    BigDecimal newMultiply = nominal.divide(valute.value(), 6, RoundingMode.HALF_UP);

                    multiplies.put(currencyCode, newMultiply);
                } else {
                    log.warn("Валюта {} не найдена в ответе ЦБ РФ", currencyCode);
                }
            }

            return multiplies;

        } catch (Exception e) {
            log.error("Критическая ошибка при отправке Webhook запроса в ЦБ", e);
            return Collections.emptyMap();
        }
    }
}
