package dto.balance;

import java.math.BigDecimal;
import java.time.Instant;

public record RequestPayoutDto(
        Long storeId,
        String transactionUuid,

        BigDecimal amount,
        String currency,

        boolean isHolding,

        Instant processedAt
) {
}
