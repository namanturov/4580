package com.example.paymentservice.service;

import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.enums.CurrencyType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface PaymentService {
    void createPayment(UUID orderId, BigDecimal amount, CurrencyType currency);

    List<Payment> getAllPayments();

    Payment getPaymentById(UUID id);

    void refundPayment(UUID id);
}
