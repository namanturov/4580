package com.example.orderservice.saga;

import com.example.orderservice.infrastructure.header.Headers;
import com.example.orderservice.infrastructure.kafka.config.KafkaTopicsProperties;
import com.example.orderservice.infrastructure.kafka.listener.IdempotentKafkaListener;
import com.example.orderservice.saga.dto.delivery.DeliveryCreateCommand;
import com.example.orderservice.saga.dto.orchestrator.OrderCreationStatusEvent;
import com.example.orderservice.saga.dto.order.OrderApproveCommand;
import com.example.orderservice.saga.dto.order.OrderCancelCommand;
import com.example.orderservice.saga.dto.payment.PaymentCreateCommand;
import com.example.orderservice.saga.dto.payment.PaymentRefundCommand;
import com.example.orderservice.service.AsyncMessageService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderCreationSagaOrchestrator extends IdempotentKafkaListener<OrderCreationStatusEvent> {

    KafkaTemplate<String, String> kafkaTemplate;
    KafkaTopicsProperties kafkaTopicsProps;
    JsonMapper jsonMapper;

    public OrderCreationSagaOrchestrator(AsyncMessageService asyncMessageService,
                                         JsonMapper jsonMapper,
                                         KafkaTemplate<String, String> kafkaTemplate,
                                         KafkaTopicsProperties kafkaTopicsProps,
                                         JsonMapper jsonMapper1) {
        super(asyncMessageService, jsonMapper);
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTopicsProps = kafkaTopicsProps;
        this.jsonMapper = jsonMapper1;
    }

    @Override
    @KafkaListener(
            topics = "${integration.kafka.topics.saga.order-creation-status}",
            groupId = "saga-order-creation-status-group")
    public void consume(ConsumerRecord<String, String> consumerRecord,
                        String message,
                        Acknowledgment ack) {
        super.consume(consumerRecord, message, ack);
    }

    @Override
    protected void processConsumedMessage(OrderCreationStatusEvent event) {
        var eventStatus = event.status();
        var orderId = event.orderId();

        log.info("Событие saga-orchestrator (order-creation) по order-id ({})", orderId);
        switch (eventStatus) {
            case ORDER_CREATED -> {
                log.info("Заявка была создана и передана платежке на обработку.");
                sendPaymentCreateCommand(orderId);
            }
            case PAYMENT_CONFIRMED -> {
                log.info("Платеж по заявке был обработан корректно и далее передан на доставку до пункта назначения");
                sendDeliveryCreateCommand(orderId);
            }
            case PAYMENT_FAILED -> {
                log.info("Платеж по заявке был фатален и возбуждены компенсирующие действия");
                sendOrderRollbackCommand(orderId);
            }
            case DELIVERY_COMPLETED -> {
                log.info("Доставка по заявке успешно создана и доставлена - приказ на approve order");
                sendOrderApproveCommand(orderId);
            }
            case DELIVERY_FAILED -> {
                log.info("Доставка по заявке был фатален и возбуждены компенсирующие действия");
                sendPaymentRollbackCommand(orderId);
                sendOrderRollbackCommand(orderId);
            }
            default -> log.warn("Какой то не понятный статус ({}) залетел у нас тут эт самое", event.status());
        }
    }

    private void sendPaymentCreateCommand(UUID orderId) {
        var event = PaymentCreateCommand.builder()
                .orderId(orderId)
                .price(BigDecimal.TEN)
                .currencyType("KGS")
                .build();

        kafkaTemplate.send(
                kafkaTopicsProps.payment().create(),
                orderId.toString(),
                jsonMapper.writeValueAsString(event));
    }

    private void sendDeliveryCreateCommand(UUID orderId) {
        var event = DeliveryCreateCommand.builder()
                .orderId(orderId)
                .build();

        kafkaTemplate.send(
                kafkaTopicsProps.delivery().create(),
                orderId.toString(),
                jsonMapper.writeValueAsString(event));
    }

    private void sendOrderRollbackCommand(UUID orderId) {
        var event = OrderCancelCommand.builder()
                .orderId(orderId)
                .build();

        Message<String> message = MessageBuilder
                .withPayload(jsonMapper.writeValueAsString(event))
                .setHeader(KafkaHeaders.TOPIC, kafkaTopicsProps.order().cancel())
                .setHeader(KafkaHeaders.KEY, orderId.toString())
                .setHeader(Headers.IDEMPOTENCY_KEY, UUID.randomUUID())
                .build();

        kafkaTemplate.send(message);
    }

    private void sendOrderApproveCommand(UUID orderId) {
        var event = OrderApproveCommand.builder()
                .orderId(orderId)
                .build();

        Message<String> message = MessageBuilder
                .withPayload(jsonMapper.writeValueAsString(event))
                .setHeader(KafkaHeaders.TOPIC, kafkaTopicsProps.order().approve())
                .setHeader(KafkaHeaders.KEY, orderId.toString())
                .setHeader(Headers.IDEMPOTENCY_KEY, UUID.randomUUID())
                .build();

        kafkaTemplate.send(message);
    }

    private void sendPaymentRollbackCommand(UUID orderId) {
        var event = PaymentRefundCommand.builder()
                .orderId(orderId)
                .build();

        kafkaTemplate.send(
                kafkaTopicsProps.payment().refund(),
                orderId.toString(),
                jsonMapper.writeValueAsString(event));
    }

    @Override
    protected Class<OrderCreationStatusEvent> getPayloadClass() {
        return OrderCreationStatusEvent.class;
    }
}
