package com.example.deliveryservice.intergration.ordercreation.kafka.dto;

import com.example.deliveryservice.intergration.ordercreation.kafka.enums.OrderCreationStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record OrderCreationStatusEvent(
        UUID deliveryId,
        UUID orderId,
        OrderCreationStatus status
) {
}
