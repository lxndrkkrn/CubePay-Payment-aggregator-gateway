package org.example.service.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "balances", indexes = {
        @Index(name = "idx_balance_uuid", columnList = "uuid", unique = true),
        // Уникальный индекс: у одного магазина может быть только один баланс в конкретной валюте
        @Index(name = "idx_store_currency", columnList = "storeId, currency", unique = true)
})
@ToString
@Getter
@Setter
public class Balance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotBlank
    @Column(unique = true, nullable = false)
    private String uuid;

    @Column(unique = true)
    private Long storeId;

    @NotNull
    @NotBlank
    private String status = "NEW"; // ACTIVE, DISABLE, BANNED...

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private BigDecimal availableBalance = BigDecimal.ZERO;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
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
