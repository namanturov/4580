package com.example.orderservice.integration.delivery.kafka.dto.request;

import lombok.Builder;

import java.util.UUID;

@Builder
public record OrderCompletedEvent(
        UUID orderId
) {
}
