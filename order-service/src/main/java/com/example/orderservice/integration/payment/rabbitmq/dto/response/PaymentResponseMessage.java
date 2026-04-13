package com.example.orderservice.integration.payment.rabbitmq.dto.response;

import com.example.orderservice.integration.payment.enums.PaymentStatus;

import java.util.UUID;

public record PaymentResponseMessage(
        UUID orderId,
        PaymentStatus status
) {
}
