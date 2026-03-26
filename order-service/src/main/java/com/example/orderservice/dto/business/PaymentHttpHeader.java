package com.example.orderservice.dto.business;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaymentHttpHeader {
    public static final String IDEMPOTENCY = "X-Idempotency-Key";
}
