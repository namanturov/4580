package com.example.orderservice.integration.payment.rabbitmq.dto.request;

import com.example.orderservice.entity.Order;
import com.example.orderservice.integration.payment.enums.CurrencyType;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record PaymentRequestMessage(
        UUID orderId,
        MoneyRequest money
) {

    @Builder
    public record MoneyRequest(
            BigDecimal amount,
            CurrencyType currency) {
    }

    public static PaymentRequestMessage forCreate(Order order) {
        return PaymentRequestMessage.builder()
                .orderId(order.getId())
                .money(MoneyRequest.builder()
                        .amount(BigDecimal.TEN)
                        .currency(CurrencyType.USD)
                        .build())
                .build();
    }
}
