package com.example.orderservice.infrastructure.header;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Headers {
    public static final String IDEMPOTENCY_KEY = "X-Idempotency-Key";
}
