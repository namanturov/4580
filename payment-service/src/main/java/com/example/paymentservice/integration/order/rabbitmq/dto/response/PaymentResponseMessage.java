package com.example.paymentservice.integration.order.rabbitmq.dto.response;

import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.enums.PaymentStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record PaymentResponseMessage(
        UUID orderId,
        PaymentStatus status
) {
    public static PaymentResponseMessage forStatusUpdated(Payment payment) {
        return PaymentResponseMessage.builder()
                .orderId(payment.getOrderId())
                .status(payment.getStatus())
                .build();
    }
}
