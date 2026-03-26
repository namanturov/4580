package com.example.paymentservice.dto.business;

import com.example.paymentservice.enums.PaymentStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment {
    UUID id;
    UUID orderId;
    PaymentStatus status;
    PaymentStatus prevStatus;
    Money money;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
