package com.example.orderservice.integration.ordercreation.kafka.dto;

import com.example.orderservice.integration.ordercreation.kafka.enums.OrderCreationStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record OrderCreationStatusEvent(
        UUID orderId,
        OrderCreationStatus status,
        BigDecimal price
) {
    public static OrderCreationStatusEvent forOrderCreated(UUID orderId) {
        return OrderCreationStatusEvent.builder()
                .orderId(orderId)
                .price(BigDecimal.TEN)
                .status(OrderCreationStatus.ORDER_CREATED)
                .build();
    }

    public static OrderCreationStatusEvent forCancel(UUID orderId){
        return OrderCreationStatusEvent.builder()
                .orderId(orderId)
                .status(OrderCreationStatus.CANCEL)
                .build();
    }
}
