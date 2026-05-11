package com.example.orderservice.integration.ordercreation.kafka.listener;

import com.example.orderservice.enums.OrderStatus;
import com.example.orderservice.infrastructure.kafka.listener.IdempotentKafkaListener;
import com.example.orderservice.integration.ordercreation.kafka.dto.OrderCreationStatusEvent;
import com.example.orderservice.integration.ordercreation.kafka.enums.OrderCreationStatus;
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

import java.util.EnumSet;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderCreationStatusListener extends IdempotentKafkaListener<OrderCreationStatusEvent> {

    private static final EnumSet<OrderCreationStatus> ERROR_STATUSES = EnumSet.of(
            OrderCreationStatus.DELIVERY_FAILED,
            OrderCreationStatus.PAYMENT_FAILED);

    OrderService orderService;

    public OrderCreationStatusListener(AsyncMessageService asyncMessageService,
                                       JsonMapper jsonMapper,
                                       OrderService orderService) {
        super(asyncMessageService, jsonMapper);
        this.orderService = orderService;
    }

    @Override
    @KafkaListener(
            topics = "${integration.kafka.topics.order.creation-status}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, String> consumerRecord,
                        String message,
                        Acknowledgment ack) {
        super.consume(consumerRecord, message, ack);
    }

    @Override
    protected void processConsumedMessage(OrderCreationStatusEvent event) {
        var eventStatus = event.status();

        if (OrderCreationStatus.PAYMENT_CONFIRMED.equals(eventStatus)) {
            orderService.updateOrderStatus(event.orderId(), OrderStatus.PAID);
            log.info("Заказ ({}) успешно оплачен", event.orderId());
        } else if (OrderCreationStatus.DELIVERY_COMPLETED.equals(eventStatus)) {
            orderService.updateOrderStatus(event.orderId(), OrderStatus.SUCCESS);
            log.info("Заказ ({}) успешно доставлен и завершен цикл заявки", event.orderId());
        } else if (ERROR_STATUSES.contains(eventStatus)) {
            orderService.cancelOrder(event.orderId());
            log.info("Заказ ({}) по нему будет возбуждено компенсирующие операции. В связи со статусом ошибки ({})",
                    event.orderId(),
                    eventStatus);
        }
    }

    @Override
    protected Class<OrderCreationStatusEvent> getPayloadClass() {
        return OrderCreationStatusEvent.class;
    }
}
