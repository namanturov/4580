package com.example.paymentservice.service.impl;

import com.example.paymentservice.entity.IdempotencyStore;
import com.example.paymentservice.enums.IdempotencyStatus;
import com.example.paymentservice.exception.IdempotencyProcessingException;
import com.example.paymentservice.repository.manager.IdempotencyManager;
import com.example.paymentservice.service.IdempotencyService;
import jakarta.persistence.OptimisticLockException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdempotencyServiceImpl implements IdempotencyService {


    @NonFinal
    @Value("${idempotency.ttl}")
    Duration ttl;

    IdempotencyManager idempotencyManager;

    @Override
    public IdempotencyStore tryGet(UUID key, String requestBody) {
        String requestHash = hash(requestBody);
        var timeNow = Instant.now();
        try {
            var expiresAt = timeNow.plus(ttl);
            var entity = new IdempotencyStore()
                    .setKey(key)
                    .setRequestHash(requestHash)
                    .setStatus(IdempotencyStatus.PROCESSING)
                    .setCreatedAt(timeNow)
                    .setExpiresAt(expiresAt);
            idempotencyManager.save(entity);
            return entity;
        } catch (DataIntegrityViolationException e) {
            var entity = idempotencyManager.getByKey(key);
            if (!requestHash.equals(entity.getRequestHash())) {
                var errorMessage = "Переданные данные не совпадают с ранее сохранёнными для данного idempotency key";
                throw new IdempotencyProcessingException(errorMessage, HttpStatus.CONFLICT);
            }
            if (timeNow.isAfter(entity.getExpiresAt())) {
                try {
                    entity
                            .setStatus(IdempotencyStatus.PROCESSING)
                            .setExpiresAt(timeNow.plus(ttl));

                    idempotencyManager.update(entity);
                    return entity;
                } catch (OptimisticLockException innerE) {
                    var errorMessage = "Ключ уже был перехвачен другим процессом";
                    throw new IdempotencyProcessingException(errorMessage, HttpStatus.CONFLICT);
                }
            }
            if (IdempotencyStatus.PROCESSING.equals(entity.getStatus())) {
                var errorMessage = "данные по данному ключу уже в процессе обработки";
                throw new IdempotencyProcessingException(errorMessage, HttpStatus.CONFLICT);
            }

            return entity;
        }
    }

    @Override
    public void markAsDone(UUID key, String responseBody, int responseStatus) {
        var entity = idempotencyManager.getByKey(key);
        if (!IdempotencyStatus.PROCESSING.equals(entity.getStatus()))
            return;
        entity
                .setStatus(IdempotencyStatus.DONE)
                .setResponseBody(responseBody)
                .setResponseStatus(responseStatus)
                .setUpdatedAt(Instant.now());
        idempotencyManager.update(entity);
    }

    private String hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            log.error("при выведении хэш request body выявилась ошибка: {}", e.getMessage(), e);
            var errorMessage = "мы уже работает над устранением";
            throw new IdempotencyProcessingException(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
