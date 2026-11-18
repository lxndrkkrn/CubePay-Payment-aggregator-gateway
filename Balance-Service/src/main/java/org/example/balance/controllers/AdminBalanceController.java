package org.example.balance.controllers;

import dto.balance.ChangeBalanceStatusDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.balance.dtos.request.ChangeBalanceStatusControllerDto;
import org.example.balance.servicies.application.balance.BalanceLifecycleAppService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/balances/admin")

public class AdminBalanceController {

    private final BalanceLifecycleAppService balanceLifecycleAppService;

    @PostMapping("/system")
    public ResponseEntity<?> createSystemBalance() {
        Long systemBalanceId = balanceLifecycleAppService.createSystemBalance();

        return ResponseEntity.ok(systemBalanceId);
    }

    @PatchMapping("/status/{id}")
    public ResponseEntity<?> changeStatusBalance(@PathVariable Long id, @Valid @RequestBody ChangeBalanceStatusControllerDto statusDto) {
        ChangeBalanceStatusDto dto = new ChangeBalanceStatusDto(id, statusDto.status());

        balanceLifecycleAppService.changeBalanceStatus(dto);

        return ResponseEntity.ok().build();
    }

}
