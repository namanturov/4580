package com.example.orderservice.integration.delivery.kafka.dto;

import java.util.UUID;

public record DeliveryCreatedEvent(
        UUID deliveryId,
        UUID orderId
) {
}
