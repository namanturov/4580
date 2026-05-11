package com.example.paymentservice.service.impl;

import com.example.paymentservice.entity.Money;
import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.enums.CurrencyType;
import com.example.paymentservice.enums.PaymentStatus;
import com.example.paymentservice.infrastructure.header.Headers;
import com.example.paymentservice.infrastructure.kafka.config.props.KafkaTopicsProperties;
import com.example.paymentservice.integration.ordercreation.dto.orchestrator.OrderCreationStatusEvent;
import com.example.paymentservice.integration.ordercreation.dto.orchestrator.enums.OrderCreationStatus;
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
//        UUID mockId = UUID.fromString("ec1f3f02-71c2-42f8-acb8-622508393322"); //для тестирования mock val
        UUID notMockId = UUID.randomUUID();
        var event = OrderCreationStatusEvent.builder()
                .orderId(orderId)
                .status(notMockId.equals(orderId)
                        ? OrderCreationStatus.PAYMENT_FAILED
                        : OrderCreationStatus.PAYMENT_CONFIRMED)
                .build();

        Message<String> message = MessageBuilder
                .withPayload(jsonMapper.writeValueAsString(event))
                .setHeader(KafkaHeaders.TOPIC, kafkaTopicsProps.saga().orderCreationStatus())
                .setHeader(KafkaHeaders.KEY, orderId.toString())
                .setHeader(Headers.IDEMPOTENCY_KEY, notMockId)
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
    public void refundPayment(UUID orderId) {
        var payment = paymentManager.getByOrderId(orderId);

        paymentManager.delete(payment);
    }
}
