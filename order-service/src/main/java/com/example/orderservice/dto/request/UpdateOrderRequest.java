package com.example.orderservice.dto.request;

import com.example.orderservice.enums.OrderStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateOrderRequest {
    String customerName;
    OrderStatus status;
}