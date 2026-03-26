package com.example.paymentservice.dto.request;

import com.example.paymentservice.enums.PaymentStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdatePaymentRequest {
    UUID orderId;
    PaymentStatus status;
    UpdateMoneyRequest money;
}
