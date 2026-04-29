package com.example.deliveryservice.intergration.order.kafka.dto.request;

import java.util.UUID;

public record OrderCompletedEvent(
        UUID orderId
) {

}
