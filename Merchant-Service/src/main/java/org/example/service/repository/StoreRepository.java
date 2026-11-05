package org.example.service.repository;

import org.example.service.entities.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {

    Optional<Store> findByUuid(String uuid);

    Optional<Store> findBySecretKey(String secretKey);

    Optional<Store> findByBalanceId(Long balanceId);

}
