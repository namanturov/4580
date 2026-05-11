package com.example.orderservice.integration.ordercreation.listener;

import com.example.orderservice.infrastructure.kafka.listener.IdempotentKafkaListener;
import com.example.orderservice.saga.dto.order.OrderCancelCommand;
import com.example.orderservice.service.AsyncMessageService;
import com.example.orderservice.service.OrderService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderCancelCommandListener extends IdempotentKafkaListener<OrderCancelCommand> {
    OrderService orderService;

    public OrderCancelCommandListener(AsyncMessageService asyncMessageService,
                                      JsonMapper jsonMapper,
                                      OrderService orderService) {
        super(asyncMessageService, jsonMapper);
        this.orderService = orderService;
    }

    @Override
    @KafkaListener(
            topics = "${integration.kafka.topics.order.cancel}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, String> consumerRecord,
                        String message,
                        Acknowledgment ack) {
        super.consume(consumerRecord, message, ack);
    }

    @Override
    protected void processConsumedMessage(OrderCancelCommand event) {
        log.info("Event на отмену по order-id ({})", event.orderId());
        orderService.cancelOrder(event.orderId());
        log.info("Заказ был отменен");
    }

    @Override
    protected Class<OrderCancelCommand> getPayloadClass() {
        return OrderCancelCommand.class;
    }
}
