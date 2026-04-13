package com.example.paymentservice.service;


import com.example.paymentservice.entity.IdempotencyStore;

import java.util.UUID;

public interface IdempotencyService {
    IdempotencyStore tryGetKey(UUID key, String requestBody);

    void markAsDone(UUID key, String responseBody, int responseStatus);
}
