package com.example.deliveryservice.intergration.order.kafka.consumer;

import com.example.deliveryservice.entity.Delivery;
import com.example.deliveryservice.intergration.order.kafka.dto.request.OrderCompletedEvent;
import com.example.deliveryservice.service.DeliveryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderConsumer {

    DeliveryService deliveryService;

    @KafkaListener(
            topics = "${integration.kafka.order.topics.completed}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consume(OrderCompletedEvent event,
                        Acknowledgment ack) {
        log.info("Получено событие завершения заказа. orderId={}", event.orderId());

        Delivery delivery = deliveryService.createDelivery(event.orderId());

        log.info("Доставка создана. deliveryId={}", delivery.getId());

        ack.acknowledge();

        log.info("Событие успешно обработано и подтверждено. orderId={}", event.orderId());
    }
}
