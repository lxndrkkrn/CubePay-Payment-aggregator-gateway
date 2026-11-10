package org.example.balance.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.example.balance.enums.balance.BalanceStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "balances", indexes = {
        @Index(name = "idx_balance_uuid", columnList = "uuid", unique = true),
})
@ToString
@Getter
@Setter
public class Balance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true, nullable = false)
    private String uuid;

    @Column(unique = true)
    private Long storeId;

    @NotNull
    @NotBlank
    @Enumerated(EnumType.STRING)
    private BalanceStatus status = BalanceStatus.NEW;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal availableBalance = BigDecimal.ZERO;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal holdBalance = BigDecimal.ZERO;

    @NotNull
    @Column(nullable = false)
    private Boolean isSystem = false;

    @PrePersist
    private void init() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID().toString();
        }
    }
}
