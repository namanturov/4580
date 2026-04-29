package com.example.deliveryservice.intergration.order.kafka.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DeliveryCreatedEvent(
        UUID deliveryId,
        UUID orderId
) {
}
