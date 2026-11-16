package dto.balance;

import java.math.BigDecimal;
import java.time.Instant;

public record ConfirmPayoutDto(
        String transactionUuid,
        String requestTransactionUuid,
        Long storeId,

        BigDecimal amount,
        String currency
) {
}
