package org.example.balance.servicies.application.balance;

import dto.balance.GetBalanceHistoryByPeriodAndPageDto;
import dto.balance.GetStoreBalanceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.balance.dtos.response.balance.BalanceHistoryByPeriodAndPageDto;
import org.example.balance.dtos.response.balance.HistoryItemDto;
import org.example.balance.dtos.response.balance.StoreBalanceInfoResponse;
import org.example.balance.entities.Balance;
import org.example.balance.entities.BalanceCurrency;
import org.example.balance.entities.HistoryBalance;
import org.example.balance.repositories.BalanceCurrencyRepository;
import org.example.balance.repositories.HistoryBalanceRepository;
import org.example.balance.repositories.outbox.BalanceOutboxRepository;
import org.example.balance.servicies.domain.balance.FindBalanceService;
import org.example.balance.servicies.domain.history.FindHistoryBalanceService;
import org.example.balance.servicies.domain.outbox.BalanceOutboxService;
import org.example.exceptions.domain.client.BadRequest;
import org.example.exceptions.domain.client.NotFound;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor

public class BalanceQueryAppService {

    private final FindBalanceService findBalanceService;
    private final FindHistoryBalanceService findHistoryBalanceService;
    private final BalanceCurrencyRepository balanceCurrencyRepository;

    @Transactional(readOnly = true)
    public StoreBalanceInfoResponse getStoreBalanceInfo(GetStoreBalanceDto dto) {
        if (dto == null) {
            throw new BadRequest("Запрос не может быть пустым");
        }
        log.info("Запрос на получение баланса: storeId={}, balanceId={}, uuid={}",
                dto.storeId(), dto.balanceId(), dto.balanceUuid());

        Balance balance = resolveBalance(dto.balanceId(), dto.balanceUuid(), dto.storeId());

        Map<String, BigDecimal> multiplyExchangeRate = balanceCurrencyRepository.findAll().stream()
                .collect(Collectors.toMap(
                        k -> String.valueOf(k.getCurrency()),
                        BalanceCurrency::getMultiply,
                        (existing, replacement) -> replacement
                ));

        return new StoreBalanceInfoResponse(
                balance.getStoreId(),
                balance.getId(),
                balance.getUuid(),
                balance.getStatus().name(),
                balance.getAvailableBalance(),
                balance.getHoldBalance(),
                multiplyExchangeRate,
                LocalDateTime.now()
        );
    }

    @Transactional(readOnly = true)
    public BalanceHistoryByPeriodAndPageDto getBalanceHistoryByPeriodAndPage(GetBalanceHistoryByPeriodAndPageDto dto) {
        validateHistoryRequest(dto);
        log.info("Запрос истории операций за период с {} по {} (стр. {})", dto.start(), dto.end(), dto.page());

        Balance balance = resolveBalance(dto.balanceId(), dto.balanceUuid(), dto.storeId());
        Pageable pageable = PageRequest.of(dto.page(), dto.pageSize());

        Page<HistoryItemDto> historyDtoPage = findHistoryBalanceService
                .findAllByBalanceIdAndCreatedAtBetweenOrderByCreatedAtDesc(
                        balance.getId(),
                        dto.start().atZone(ZoneId.systemDefault()).toInstant(),
                        dto.end().atZone(ZoneId.systemDefault()).toInstant(),
                        pageable
                )
                .map(this::mapToHistoryItemDto);

        return new BalanceHistoryByPeriodAndPageDto(
                balance.getStoreId(),
                balance.getId(),
                dto.page(),
                dto.pageSize(),
                dto.start(),
                dto.end(),
                historyDtoPage
        );
    }

    private Balance resolveBalance(Long balanceId, String balanceUuid, Long storeId) {
        if (balanceId != null && balanceId > 0) {
            return findBalanceService.findById(balanceId);
        }
        if (balanceUuid != null && !balanceUuid.isBlank()) {
            return findBalanceService.findByUuid(balanceUuid);
        }
        if (storeId != null && storeId > 0) {
            return findBalanceService.findByStoreId(storeId);
        }
        throw new BadRequest("Необходимо указать хотя бы один идентификатор: storeId, balanceId или balanceUuid");
    }

    private void validateHistoryRequest(GetBalanceHistoryByPeriodAndPageDto dto) {
        if (dto == null || dto.start() == null || dto.end() == null) {
            throw new BadRequest("Необходимо указать период выборки (start и end)");
        }
        if (dto.start().isAfter(dto.end())) {
            throw new BadRequest("Дата начала периода не может быть позже даты окончания");
        }
        if (dto.page() == null || dto.page() < 0 || dto.pageSize() == null || dto.pageSize() <= 0 || dto.pageSize() > 100) {
            throw new BadRequest("Некорректные параметры пагинации (page >= 0, 1 <= pageSize <= 100)");
        }
    }

    private HistoryItemDto mapToHistoryItemDto(HistoryBalance entity) {
        return new HistoryItemDto(
                entity.getUuid(),
                entity.getOperationType().name(),
                entity.getBucketType().name(),
                entity.getAmount(),
                entity.getBalanceBefore(),
                entity.getBalanceAfter(),
                entity.getDescription(),
                entity.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()
        );
    }
}
