package com.example.paymentservice.repository.manager;

import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.exception.EntityNotFoundException;
import com.example.paymentservice.repository.PaymentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentManager {

    PaymentRepository paymentRepository;

    @Transactional
    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

    public List<Payment> getAll() {
        return paymentRepository.findAll();
    }

    public Payment getById(UUID id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> {
                    var errorMessage = String.format("данный платеж не был найден по id - %s", id);
                    log.error(errorMessage);
                    return new EntityNotFoundException(errorMessage);
                });
    }

    @Transactional
    public void delete(Payment payment) {
        paymentRepository.delete(payment);
    }
}
