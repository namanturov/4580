package com.example.orderservice.integration.ordercreation.listener;

import com.example.orderservice.enums.OrderStatus;
import com.example.orderservice.infrastructure.kafka.listener.IdempotentKafkaListener;
import com.example.orderservice.saga.dto.order.OrderApproveCommand;
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
public class OrderApproveCommandListener extends IdempotentKafkaListener<OrderApproveCommand> {
    OrderService orderService;

    public OrderApproveCommandListener(AsyncMessageService asyncMessageService,
                                       JsonMapper jsonMapper,
                                       OrderService orderService) {
        super(asyncMessageService, jsonMapper);
        this.orderService = orderService;
    }

    @Override
    @KafkaListener(
            topics = "${integration.kafka.topics.order.approve}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, String> consumerRecord,
                        String message,
                        Acknowledgment ack) {
        super.consume(consumerRecord, message, ack);
    }

    @Override
    protected void processConsumedMessage(OrderApproveCommand event) {
        log.info("Event на approve по order-id ({})", event.orderId());
        orderService.updateOrderStatus(event.orderId(), OrderStatus.SUCCESS);
        log.info("Заказ ({}) успешно доставлен и завершен цикл заявки", event.orderId());
    }

    @Override
    protected Class<OrderApproveCommand> getPayloadClass() {
        return OrderApproveCommand.class;
    }
}
