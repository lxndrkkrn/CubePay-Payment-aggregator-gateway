package dto.balance;

import java.math.BigDecimal;

public record ProcessPaymentRefundDto(
        String refundUuid,
        String originalTransactionUuid,
        Long storeId,

        BigDecimal refundTotalAmount,
        BigDecimal storeDebitAmount,
        BigDecimal feeRefundAmount,

        boolean fromHoldBalance
) {
}
