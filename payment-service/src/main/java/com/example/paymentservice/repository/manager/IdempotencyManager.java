package com.example.paymentservice.repository.manager;

import com.example.paymentservice.entity.IdempotencyStore;
import com.example.paymentservice.exception.EntityNotFoundException;
import com.example.paymentservice.repository.IdempotencyRepository;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdempotencyManager {

    IdempotencyRepository repository;
    EntityManager entityManager;

    @Transactional
    public void save(IdempotencyStore entity) {
        entityManager.persist(entity);
    }

    public IdempotencyStore getByKey(UUID key) {
        return repository.findById(key)
                .orElseThrow(() -> new EntityNotFoundException("нема ключа такого idempotency"));
    }

    @Transactional
    public void update(IdempotencyStore entity) {
        repository.save(entity);
    }
}
