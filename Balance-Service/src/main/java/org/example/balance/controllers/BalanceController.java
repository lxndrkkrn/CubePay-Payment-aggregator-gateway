package org.example.balance.controllers;

import dto.balance.GetBalanceHistoryByPeriodAndPageDto;
import dto.balance.GetStoreBalanceDto;
import dto.balance.RequestPayoutDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.balance.dtos.response.balance.BalanceHistoryByPeriodAndPageDto;
import org.example.balance.dtos.response.balance.StoreBalanceInfoResponse;
import org.example.balance.servicies.application.balance.BalanceQueryAppService;
import org.example.balance.servicies.application.balance.PayoutAppService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/balances/main")

public class BalanceController {

    private final BalanceQueryAppService balanceQueryAppService;
    private final PayoutAppService payoutAppService;

    @GetMapping("/info")
    public ResponseEntity<?> getStoreBalanceInfo(
            @RequestParam(value = "storeId", required = false) Long storeId,
            @RequestParam(value = "balanceId", required = false) Long balanceId,
            @RequestParam(value = "balanceUuid", required = false) String balanceUuid
    ) {
        GetStoreBalanceDto dto = new GetStoreBalanceDto(storeId, balanceId, balanceUuid);

        StoreBalanceInfoResponse info = balanceQueryAppService.getStoreBalanceInfo(dto);

        return ResponseEntity.ok(info);
    }

    @GetMapping("/history")
    public ResponseEntity<?> getHistoryByPeriodAndPage(
            @RequestParam(value = "storeId", required = false) Long storeId,
            @RequestParam(value = "balanceId", required = false) Long balanceId,
            @RequestParam(value = "balanceUuid", required = false) String balanceUuid,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        GetBalanceHistoryByPeriodAndPageDto dto = new GetBalanceHistoryByPeriodAndPageDto(storeId, balanceId, balanceUuid, page, pageSize, start, end);

        BalanceHistoryByPeriodAndPageDto history = balanceQueryAppService.getBalanceHistoryByPeriodAndPage(dto);

        return ResponseEntity.ok(history);
    }

    @PostMapping("/payouts")
    public ResponseEntity<?> requestPayout(@Valid @RequestBody RequestPayoutDto dto) {
        payoutAppService.requestPayout(dto);

        return ResponseEntity.accepted().build();
    }

}
