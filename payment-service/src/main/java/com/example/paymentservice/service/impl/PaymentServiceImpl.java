package com.example.paymentservice.service.impl;

import com.example.paymentservice.entity.Money;
import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.enums.PaymentStatus;
import com.example.paymentservice.repository.manager.PaymentManager;
import com.example.paymentservice.service.PaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentServiceImpl implements PaymentService {

    PaymentManager paymentManager;
    ModelMapper modelMapper;

    @Override
    public void create(Payment payment) {
        payment.setStatus(PaymentStatus.CREATED);
        var paymentEntity = modelMapper.map(payment, Payment.class);

        paymentManager.save(paymentEntity);
    }

    @Override
    public List<Payment> getAll() {
        var paymentEntityList = paymentManager.getAll();

        return paymentEntityList.stream()
                .map(payment -> modelMapper.map(payment, Payment.class))
                .toList();
    }

    @Override
    public Payment getById(UUID id) {
        var paymentEntity = paymentManager.getById(id);

        return modelMapper.map(paymentEntity, Payment.class);
    }

    @Override
    public void update(UUID id, Payment payment) {
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
    public void delete(UUID id) {
        var paymentEntity = paymentManager.getById(id);

        paymentManager.delete(paymentEntity);
    }
}
