package com.example.deliveryservice.service.impl;

import com.example.deliveryservice.entity.Delivery;
import com.example.deliveryservice.enums.DeliveryStatus;
import com.example.deliveryservice.infrastructure.header.Headers;
import com.example.deliveryservice.infrastructure.kafka.config.KafkaTopicsProperties;
import com.example.deliveryservice.intergration.ordercreation.kafka.dto.OrderCreationStatusEvent;
import com.example.deliveryservice.intergration.ordercreation.kafka.enums.OrderCreationStatus;
import com.example.deliveryservice.repository.manager.DeliveryManager;
import com.example.deliveryservice.service.DeliveryService;
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

import java.util.List;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeliveryServiceImpl implements DeliveryService {

    KafkaTemplate<String, String> kafkaTemplate;
    KafkaTopicsProperties kafkaTopicsProps;
    DeliveryManager deliveryManager;
    JsonMapper jsonMapper;

    @Override
    public List<Delivery> getAllDeliveries() {
        return deliveryManager.getAll();
    }

    @Override
    public Delivery getDeliveryById(UUID id) {
        return deliveryManager.getById(id);
    }

    @Override
    public void deleteDelivery(UUID orderId) {
        var delivery = deliveryManager.getByOrderId(orderId);

        deliveryManager.delete(delivery);
    }

    @Override
    public void createDelivery(UUID orderId) {
        log.info("Начало создания доставки по заказу: {}", orderId);

        var delivery = new Delivery()
                .setOrderId(orderId)
                .setStatus(DeliveryStatus.CREATED);
        var savedDelivery = deliveryManager.save(delivery);

        log.info("Доставка создана. ID доставки: {}", savedDelivery.getId());

        publishStatusEvent(savedDelivery);
    }

    private void publishStatusEvent(Delivery delivery) {
        UUID mockId = UUID.fromString("29970912-da34-47d6-964b-1fa113db45c5");
        var event = OrderCreationStatusEvent.builder()
                .deliveryId(delivery.getId())
                .orderId(delivery.getOrderId())
                .status(mockId.equals(delivery.getOrderId())
                        ? OrderCreationStatus.DELIVERY_FAILED
                        : OrderCreationStatus.DELIVERY_COMPLETED)
                .build();

        Message<String> message = MessageBuilder
                .withPayload(jsonMapper.writeValueAsString(event))
                .setHeader(KafkaHeaders.TOPIC, kafkaTopicsProps.order().creationStatus())
                .setHeader(KafkaHeaders.KEY, delivery.getId().toString())
                .setHeader(Headers.IDEMPOTENCY_KEY, mockId)//для тестирования mock val
                .build();

        kafkaTemplate.send(message);

        log.info("Отправлено событие создания доставки в Kafka. deliveryId={}", delivery.getId());
    }
}
