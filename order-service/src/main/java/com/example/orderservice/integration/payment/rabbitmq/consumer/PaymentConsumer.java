package com.example.orderservice.integration.payment.rabbitmq.consumer;

import com.example.orderservice.integration.payment.rabbitmq.dto.response.PaymentResponseMessage;
import com.example.orderservice.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentConsumer {

    OrderService orderService;

    @RabbitListener(queues = "${integration.rabbitmq.payment.payment-status.queue}")
    public void handle(PaymentResponseMessage responseMessage) {
        log.info("получен ответ с платежной системы: response={}", responseMessage);
        orderService.updateOrderStatus(responseMessage.orderId(), responseMessage.status());
    }
}
