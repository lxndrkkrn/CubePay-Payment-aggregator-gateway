package dto.balance;

import java.math.BigDecimal;

public record CompensateFailedRefundDto(
        String refundUuid,
        String originalTransactionUuid,
        Long storeId,

        BigDecimal storeReturnAmount,
        BigDecimal feeReturnAmount,

        boolean toHoldBalance,
        String failureReason
) {
}
