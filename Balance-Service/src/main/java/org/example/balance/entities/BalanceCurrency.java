package org.example.balance.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.example.balance.enums.balance.Currency;

import java.math.BigDecimal;

@Entity
@Table(name = "balance_currencies")
@ToString
@Getter
@Setter

public class BalanceCurrency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @NotNull
    @Positive
    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal multiply;

}
