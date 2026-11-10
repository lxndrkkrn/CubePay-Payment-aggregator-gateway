package org.example.balance.entities.outbox;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "balance_outboxes")
@Getter
@Setter
@ToString

public class BalanceOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDateTime createdAt = LocalDateTime.now();

    @NotNull
    private String idempotentKey;

    @NotNull @NotBlank
    private String status = "NEW";

    @NotNull @NotBlank
    private String eventType;

    @NotNull @NotBlank
    private String payload;

    @PrePersist
    private void init() {
        if (this.idempotentKey == null) {
            this.idempotentKey = UUID.randomUUID().toString();
        }
    }

}
