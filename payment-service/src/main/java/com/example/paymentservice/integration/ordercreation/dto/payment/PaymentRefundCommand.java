package com.example.paymentservice.integration.ordercreation.dto.payment;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PaymentRefundCommand(
        UUID orderId
) {
}
