package com.example.orderservice.integration.dto.request;

import com.example.orderservice.integration.dto.enums.CurrencyType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatePaymentRequest {
    UUID orderId;
    CreateMoneyRequest money;

    public static CreatePaymentRequest of(UUID orderId,
                                          BigDecimal amount,
                                          CurrencyType currencyType) {
        return new CreatePaymentRequest()
                .setOrderId(orderId)
                .setMoney(new CreateMoneyRequest()
                        .setAmount(amount)
                        .setCurrency(currencyType));
    }
}