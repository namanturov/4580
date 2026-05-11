package com.example.paymentservice.integration.ordercreation.dto.orchestrator;

import com.example.paymentservice.integration.ordercreation.dto.orchestrator.enums.OrderCreationStatus;
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
