package com.example.orderservice.dto.response;

import com.example.orderservice.enums.OrderStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    UUID id;
    String customerName;
    OrderStatus status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
