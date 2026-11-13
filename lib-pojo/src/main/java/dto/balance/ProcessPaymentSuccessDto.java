package dto.balance;

import java.math.BigDecimal;
import java.time.Instant;

public record ProcessPaymentSuccessDto(
        String transactionUuid,
        Long storeId,

        BigDecimal totalAmount,
        BigDecimal storeAmount,
        BigDecimal gatewayFee,
        BigDecimal bankFee,

        boolean isHoldRequired,
        Instant processedAt

) {
}
