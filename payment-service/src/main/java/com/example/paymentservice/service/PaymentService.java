package com.example.paymentservice.service;

import com.example.paymentservice.dto.request.UpdatePaymentRequest;
import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.enums.CurrencyType;
import com.example.paymentservice.enums.PaymentStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface PaymentService {
    Payment createPayment(UUID orderId, BigDecimal amount, CurrencyType currency);

    List<Payment> getAllPayments();

    Payment getPaymentById(UUID id);

    void updatePayment(UUID id, UpdatePaymentRequest request);

    Payment updatePaymentStatus(UUID id, PaymentStatus status);

    void deletePayment(UUID id);
}
