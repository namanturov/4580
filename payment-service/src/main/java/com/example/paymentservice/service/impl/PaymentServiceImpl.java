package com.example.paymentservice.service.impl;

import com.example.paymentservice.entity.Money;
import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.enums.CurrencyType;
import com.example.paymentservice.enums.PaymentStatus;
import com.example.paymentservice.infrastructure.header.Headers;
import com.example.paymentservice.infrastructure.kafka.config.props.KafkaTopicsProperties;
import com.example.paymentservice.integration.ordercreation.kafka.dto.OrderCreationStatusEvent;
import com.example.paymentservice.integration.ordercreation.kafka.enums.OrderCreationStatus;
import com.example.paymentservice.repository.manager.PaymentManager;
import com.example.paymentservice.service.PaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentServiceImpl implements PaymentService {

    KafkaTemplate<String, String> kafkaTemplate;
    KafkaTopicsProperties kafkaTopicsProps;
    PaymentManager paymentManager;
    JsonMapper jsonMapper;


    @Override
    public void createPayment(UUID orderId, BigDecimal amount, CurrencyType currency) {
        var payment = new Payment()
                .setOrderId(orderId)
                .setMoney(new Money()
                        .setAmount(amount)
                        .setCurrency(currency))
                .setStatus(PaymentStatus.CREATED);
        paymentManager.save(payment);
        publishStatusEvent(orderId);
    }

    private void publishStatusEvent(UUID orderId) {
        UUID mockId = UUID.fromString("1bd03235-fd26-4513-9ee6-5c2ed5f34932");
        var event = OrderCreationStatusEvent.builder()
                .orderId(orderId)
                .status(mockId.equals(orderId)
                        ? OrderCreationStatus.PAYMENT_FAILED
                        : OrderCreationStatus.PAYMENT_CONFIRMED)
                .build();

        Message<String> message = MessageBuilder
                .withPayload(jsonMapper.writeValueAsString(event))
                .setHeader(KafkaHeaders.TOPIC, kafkaTopicsProps.order().creationStatus())
                .setHeader(KafkaHeaders.KEY, orderId.toString())
                .setHeader(Headers.IDEMPOTENCY_KEY, mockId)//для тестирования mock val
                .build();

        kafkaTemplate.send(message);
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
    public void deletePayment(UUID orderId) {
        var payment = paymentManager.getByOrderId(orderId);

        paymentManager.delete(payment);
    }
}
