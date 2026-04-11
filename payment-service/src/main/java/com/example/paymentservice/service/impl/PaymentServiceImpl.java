package com.example.paymentservice.service.impl;

import com.example.paymentservice.entity.Money;
import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.enums.CurrencyType;
import com.example.paymentservice.enums.PaymentStatus;
import com.example.paymentservice.repository.manager.PaymentManager;
import com.example.paymentservice.service.PaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentServiceImpl implements PaymentService {

    PaymentManager paymentManager;

    @Override
    public Payment createPayment(UUID orderId, BigDecimal amount, CurrencyType currency) {
        var payment = new Payment()
                .setOrderId(orderId)
                .setMoney(new Money()
                        .setAmount(amount)
                        .setCurrency(currency))
                .setStatus(PaymentStatus.CREATED);
        return paymentManager.save(payment);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentManager.getAll();
    }

    @Override
    public Payment getPaymentById(UUID id) {
        return paymentManager.getById(id);
    }

    @Override
    public void updatePayment(UUID id, Payment payment) {
        var paymentEntity = paymentManager.getById(id);
        paymentEntity.setStatus(payment.getStatus());
        var newMoney = new Money()
                .setAmount(payment.getMoney().getAmount())
                .setCurrency(payment.getMoney().getCurrency());
        if (!newMoney.equals(paymentEntity.getMoney())) {
            paymentEntity.setMoney(newMoney);
        }

        paymentManager.save(paymentEntity);
    }

    @Override
    public Payment updatePaymentStatus(UUID id, PaymentStatus status) {
        var payment = paymentManager.getById(id);
        payment.setStatus(status);
        return paymentManager.save(payment);
    }

    @Override
    public void deletePayment(UUID id) {
        var paymentEntity = paymentManager.getById(id);

        paymentManager.delete(paymentEntity);
    }
}
