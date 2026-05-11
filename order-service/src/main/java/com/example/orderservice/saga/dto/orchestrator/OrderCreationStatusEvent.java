package com.example.orderservice.saga.dto.orchestrator;

import com.example.orderservice.saga.dto.orchestrator.enums.OrderCreationStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record OrderCreationStatusEvent(
        UUID orderId,
        OrderCreationStatus status
) {
}
