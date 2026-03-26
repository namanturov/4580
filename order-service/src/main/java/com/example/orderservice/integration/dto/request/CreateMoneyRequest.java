package com.example.orderservice.integration.dto.request;

import com.example.orderservice.integration.dto.enums.CurrencyType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateMoneyRequest {
    BigDecimal amount;
    CurrencyType currency;
}
