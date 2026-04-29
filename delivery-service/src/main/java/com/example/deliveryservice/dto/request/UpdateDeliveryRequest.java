package com.example.deliveryservice.dto.request;

import com.example.deliveryservice.enums.DeliveryStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateDeliveryRequest {
    UUID orderId;
    DeliveryStatus status;
    UpdateAddressRequest address;
}
