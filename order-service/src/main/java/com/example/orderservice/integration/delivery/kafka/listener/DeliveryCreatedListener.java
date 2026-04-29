package com.example.orderservice.integration.delivery.kafka.listener;

import com.example.orderservice.infrastructure.kafka.listener.IdempotentKafkaListener;
import com.example.orderservice.integration.delivery.kafka.dto.DeliveryCreatedEvent;
import com.example.orderservice.service.AsyncMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
public class DeliveryCreatedListener extends IdempotentKafkaListener<DeliveryCreatedEvent> {

    public DeliveryCreatedListener(AsyncMessageService asyncMessageService,
                                   JsonMapper jsonMapper) {
        super(asyncMessageService, jsonMapper);
    }

    @Override
    @KafkaListener(
            topics = "${integration.kafka.topics.delivery.created}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, String> consumerRecord,
                        String message,
                        Acknowledgment ack) {
        super.consume(consumerRecord, message, ack);
    }

    @Override
    protected void processConsumedMessage(DeliveryCreatedEvent event) {
        log.info("Получено событие создания доставки. payload={}", event);
    }

    @Override
    protected Class<DeliveryCreatedEvent> getPayloadClass() {
        return DeliveryCreatedEvent.class;
    }
}
