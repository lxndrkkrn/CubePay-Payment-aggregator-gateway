package org.example.service.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "stores", indexes = {
        @Index(name = "idx_store_uuid", columnList = "uuid", unique = true)
})
@ToString(exclude = "secretKey") // Чтобы случайно не вывелся в логах
@Getter
@Setter
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull @NotBlank
    @Column(unique = true, nullable = false)
    private String uuid;

    @NotNull @NotBlank
    private String status = "ACTIVE";

    @NotNull
    private Long balanceId;

    @NotNull
    private Integer transactionsPerMinuteLimit = 60;

    @NotNull @NotBlank
    private String secretKey;

    @NotNull @NotEmpty
    private String name;

    @PrePersist
    private void init() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID().toString();
        }
    }
}

