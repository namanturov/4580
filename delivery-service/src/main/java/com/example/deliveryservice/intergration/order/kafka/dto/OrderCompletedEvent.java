package com.example.deliveryservice.intergration.order.kafka.dto;

import java.util.UUID;

public record OrderCompletedEvent(
        UUID orderId
) {

}
