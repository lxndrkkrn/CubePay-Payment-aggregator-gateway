package org.example.balance.servicies.application.balance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.balance.servicies.domain.balance.ChangeBalanceService;
import org.example.balance.servicies.domain.balance.FindBalanceService;
import org.example.balance.servicies.domain.outbox.BalanceOutboxService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor

// requestPayout(...) — заморозить средства под заявку на вывод (с фиксацией курса валюты).
// confirmPayout(...) — списать замороженные средства навсегда после ответа банка.
// cancelPayout(...) — вернуть средства из холда обратно в доступные при ошибке банка.

public class PayoutAppService {

    private final FindBalanceService findBalanceService;
    private final ChangeBalanceService changeBalanceService;

    private final BalanceOutboxService outboxService;

}
