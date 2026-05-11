package com.example.deliveryservice.intergration.ordercreation.listener;

import com.example.deliveryservice.intergration.ordercreation.dto.delivery.DeliveryCreateCommand;
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
public class DeliveryCreateCommandListener {

    DeliveryService deliveryService;
    JsonMapper jsonMapper;

    @KafkaListener(
            topics = "${integration.kafka.topics.delivery.create}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message,
                        Acknowledgment ack) {
        try {
            var event = jsonMapper.readValue(message, DeliveryCreateCommand.class);
            var orderId = event.orderId();

            log.info("Доставка на создание по заявке ({})", orderId);
            deliveryService.createDelivery(orderId);
            log.info("Доставка оформлена и доставлена");

            ack.acknowledge();
        } catch (Exception e) {
            log.error("Произошла ошибка во время обработки event: {}", e.getMessage(), e);
        }
    }
}
