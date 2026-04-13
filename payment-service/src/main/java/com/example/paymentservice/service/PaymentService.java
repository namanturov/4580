package com.example.paymentservice.service;

import com.example.paymentservice.dto.business.Payment;

import java.util.List;
import java.util.UUID;

public interface PaymentService {
    void create(Payment payment);

    List<Payment> getAll();

    Payment getById(UUID id);

    void update(UUID id, Payment payment);

    void delete(UUID id);
}
