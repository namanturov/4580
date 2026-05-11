package com.example.deliveryservice.intergration.ordercreation.dto.orchestrator;

import com.example.deliveryservice.intergration.ordercreation.dto.orchestrator.enums.OrderCreationStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record OrderCreationStatusEvent(
        UUID deliveryId,
        UUID orderId,
        OrderCreationStatus status
) {
}
