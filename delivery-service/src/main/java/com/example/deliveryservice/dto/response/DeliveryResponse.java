package com.example.deliveryservice.dto.response;

import com.example.deliveryservice.enums.DeliveryStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeliveryResponse {
    UUID id;
    UUID orderId;
    DeliveryStatus status;
    AddressResponse address;
}
