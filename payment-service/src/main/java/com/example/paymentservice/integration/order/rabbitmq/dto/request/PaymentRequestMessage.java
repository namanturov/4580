package com.example.paymentservice.integration.order.rabbitmq.dto.request;

import com.example.paymentservice.enums.CurrencyType;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequestMessage(
        UUID orderId,
        MoneyRequest money
) {
    public record MoneyRequest(
            BigDecimal amount,
            CurrencyType currency) {
    }
}