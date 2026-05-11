package com.example.deliveryservice.intergration.ordercreation.dto.delivery;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DeliveryCreateCommand(
        UUID orderId
) {
}
