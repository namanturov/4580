package com.example.paymentservice.integration.ordercreation.dto.payment;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record PaymentCreateCommand(
        UUID orderId,
        BigDecimal price,
        String currencyType
) {
}
