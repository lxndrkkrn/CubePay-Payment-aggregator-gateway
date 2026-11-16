package dto.balance;

import java.math.BigDecimal;
import java.time.Instant;

public record CancelPayoutDto(
        String transactionUuid,
        String requestTransactionUuid,
        Long storeId,

        BigDecimal amount,
        String currency,

        String cancelDescription
) {
}
