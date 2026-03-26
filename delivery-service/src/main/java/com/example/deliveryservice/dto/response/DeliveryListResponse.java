package com.example.deliveryservice.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeliveryListResponse {
    List<DeliveryResponse> deliveries;

    public static DeliveryListResponse of(List<DeliveryResponse> deliveryResponseList) {
        return new DeliveryListResponse()
                .setDeliveries(deliveryResponseList);
    }
}
