package com.example.orderservice.saga.dto.delivery;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DeliveryCreateCommand(
        UUID orderId
) {
}
