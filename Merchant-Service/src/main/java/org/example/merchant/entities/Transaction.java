package org.example.merchant.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@ToString
@Getter
@Setter

public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true, nullable = false)
    private String uuid;

    @NotNull
    @NotBlank
    private String clientTransactionUuid;

    @NotBlank
    private String bankTransactionUuid;

    @NotNull
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime endedAt;

    @NotNull
    @NotBlank
    private String status = "CREATED";

    @ManyToOne
    private Store store;

    @NotNull
    @Positive
    @DecimalMin("0.01")
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal commissionAmount = BigDecimal.ZERO;

    @NotNull
    @NotBlank
    @Size(min = 3, max = 3)
    private String currency;

    @NotNull
    @NotBlank
    private String clientId;

    @NotNull
    @NotBlank
    private String clientIp;

    @NotNull
    @URL
    private String callbackUrl;

    @NotNull
    @URL
    private String redirectUrl;

    @PrePersist
    private void init() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID().toString();
        }
    }
}
