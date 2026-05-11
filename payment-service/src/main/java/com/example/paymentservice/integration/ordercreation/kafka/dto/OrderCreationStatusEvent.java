package com.example.paymentservice.integration.ordercreation.kafka.dto;

import com.example.paymentservice.integration.ordercreation.kafka.enums.OrderCreationStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record OrderCreationStatusEvent(
        UUID orderId,
        OrderCreationStatus status,
        BigDecimal price
) {
}
