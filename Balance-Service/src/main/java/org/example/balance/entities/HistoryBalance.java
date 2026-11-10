package org.example.balance.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.example.balance.enums.history.HistoryBucketType;
import org.example.balance.enums.history.HistoryOperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "balance_history", indexes = {
        @Index(name = "idx_balance_uuid", columnList = "uuid", unique = true),
})
@ToString
@Getter
@Setter

public class HistoryBalance {

    // ----------- GENERAL INFO -----------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true, nullable = false)
    private String uuid;

    @NotNull
    @Column(nullable = false, updatable = false)
    private Long balanceId;

    @NotNull
    private LocalDateTime createdAt = LocalDateTime.now();

    // ----------- IDEMPOTENCE -----------

    @NotBlank
    @Column(nullable = false, unique = true, updatable = false)
    private String idempotencyKey;

    // ----------- GENERAL OPERATION INFO -----------

    @NotNull
    @Enumerated(EnumType.STRING)
    private HistoryOperationType operationType;

    @NotNull
    @Enumerated(EnumType.STRING)
    private HistoryBucketType bucketType;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount = BigDecimal.ZERO;

    // ----------- SNAPSHOT -----------

    @NotNull
    @Column(nullable = false, precision = 19, scale = 4, updatable = false)
    private BigDecimal balanceBefore;

    @NotNull
    @Column(nullable = false, precision = 19, scale = 4, updatable = false)
    private BigDecimal balanceAfter;

    // ----------- DESCRIPTION -----------

    @Column(updatable = false, length = 500)
    private String description;

    @PrePersist
    private void init() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID().toString();
        }
    }
}
