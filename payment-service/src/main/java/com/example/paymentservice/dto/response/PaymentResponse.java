package com.example.paymentservice.dto.response;

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
public class PaymentResponse {
    UUID id;
    UUID orderId;
    PaymentStatus status;
    MoneyResponse money;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
