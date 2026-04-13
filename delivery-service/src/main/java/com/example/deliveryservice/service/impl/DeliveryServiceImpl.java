package com.example.deliveryservice.service.impl;

import com.example.deliveryservice.dto.request.UpdateDeliveryRequest;
import com.example.deliveryservice.entity.Address;
import com.example.deliveryservice.entity.Delivery;
import com.example.deliveryservice.enums.DeliveryStatus;
import com.example.deliveryservice.intergration.order.kafka.dto.response.DeliveryCreatedEvent;
import com.example.deliveryservice.intergration.properties.DeliveryKafkaProperties;
import com.example.deliveryservice.repository.manager.DeliveryManager;
import com.example.deliveryservice.service.DeliveryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeliveryServiceImpl implements DeliveryService {

    KafkaTemplate<String, DeliveryCreatedEvent> kafkaTemplate;
    DeliveryKafkaProperties deliveryKafkaProps;
    DeliveryManager deliveryManager;

    @Override
    public List<Delivery> getAllDeliveries() {
        return deliveryManager.getAll();
    }

    @Override
    public Delivery getDeliveryById(UUID id) {
        return deliveryManager.getById(id);
    }

    @Override
    public void updateDelivery(UUID id, UpdateDeliveryRequest request) {
        var delivery = deliveryManager.getById(id);
        assignDeliveryEntityFull(delivery, request);

        deliveryManager.save(delivery);
    }

    private void assignDeliveryEntityFull(Delivery delivery, UpdateDeliveryRequest request) {
        delivery.setStatus(request.getStatus())
                .setOrderId(request.getOrderId());

        if (request.getAddress() != null) {
            var updateAddress = request.getAddress();
            delivery.setAddress(new Address()
                    .setCity(updateAddress.getCity())
                    .setStreet(updateAddress.getStreet())
                    .setHouse(updateAddress.getHouse()));
        }
    }

    @Override
    public void deleteDelivery(UUID id) {
        var delivery = deliveryManager.getById(id);

        deliveryManager.delete(delivery);
    }

    @Override
    public Delivery createDelivery(UUID orderId) {
        log.info("Начало создания доставки по заказу: {}", orderId);

        var delivery = new Delivery()
                .setOrderId(orderId)
                .setStatus(DeliveryStatus.CREATED);
        var savedDelivery = deliveryManager.save(delivery);

        log.info("Доставка создана. ID доставки: {}", savedDelivery.getId());

        publishDeliveryCreatedEvent(savedDelivery);

        return savedDelivery;
    }

    private void publishDeliveryCreatedEvent(Delivery delivery) {
        kafkaTemplate.send(
                deliveryKafkaProps.topics().created(),
                delivery.getId().toString(),
                DeliveryCreatedEvent.builder()
                        .deliveryId(delivery.getId())
                        .orderId(delivery.getOrderId())
                        .build()
        );

        log.info("Отправлено событие создания доставки в Kafka. deliveryId={}", delivery.getId());
    }
}
