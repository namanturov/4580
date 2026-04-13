package com.example.paymentservice.dto.request;

import com.example.paymentservice.enums.CurrencyType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateMoneyRequest {
    BigDecimal amount;
    CurrencyType currency;
}
