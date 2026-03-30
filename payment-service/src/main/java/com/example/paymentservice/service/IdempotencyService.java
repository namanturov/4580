package com.example.paymentservice.service;


import com.example.paymentservice.entity.IdempotencyStore;

import java.util.UUID;

public interface IdempotencyService {
    IdempotencyStore tryGet(UUID key, String requestBody);

    void markAsDone(UUID key, String responseBody, int responseStatus);
}
