package com.example.deliveryservice.intergration.ordercreation.kafka.listener;

import com.example.deliveryservice.intergration.ordercreation.kafka.dto.OrderCreationStatusEvent;
import com.example.deliveryservice.intergration.ordercreation.kafka.enums.OrderCreationStatus;
import com.example.deliveryservice.service.DeliveryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderCreationStatusListener {

    DeliveryService deliveryService;
    JsonMapper jsonMapper;

    @KafkaListener(
            topics = "${integration.kafka.topics.order.creation-status}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message,
                        Acknowledgment ack) {
        try {
            var event = jsonMapper.readValue(message, OrderCreationStatusEvent.class);

            if (event.status().equals(OrderCreationStatus.PAYMENT_CONFIRMED)) {
                deliveryService.createDelivery(event.orderId());
                log.info("Доставка по заявке ({}) оформлена и доставлена", event.orderId());
            } else if (event.status().equals(OrderCreationStatus.CANCEL)) {
                deliveryService.deleteDelivery(event.orderId());
                log.info("Доставка по заявке ({}) компенсация пошла поехала и done.", event.orderId());
            }

            ack.acknowledge();
        } catch (Exception e) {
            log.error("Произошла ошибка во время обработки event: {}", e.getMessage(), e);
        }
    }
}
