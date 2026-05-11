package com.example.orderservice.saga.dto.payment;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PaymentRefundCommand(
        UUID orderId
) {
}
