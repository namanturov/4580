package com.example.orderservice.saga.dto.order;

import lombok.Builder;

import java.util.UUID;

@Builder
public record OrderCancelCommand(
        UUID orderId
) {
}
