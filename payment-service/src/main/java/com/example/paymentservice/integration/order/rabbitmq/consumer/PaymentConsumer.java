package com.example.paymentservice.integration.order.rabbitmq.consumer;

import com.example.paymentservice.enums.PaymentStatus;
import com.example.paymentservice.integration.order.rabbitmq.config.properties.OrderRabbitMqProperties;
import com.example.paymentservice.integration.order.rabbitmq.dto.request.PaymentRequestMessage;
import com.example.paymentservice.integration.order.rabbitmq.dto.response.PaymentResponseMessage;
import com.example.paymentservice.service.PaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentConsumer {

    OrderRabbitMqProperties props;
    RabbitTemplate rabbitTemplate;
    PaymentService paymentService;

    @RabbitListener(queues = "${integration.rabbitmq.order.create-payment.queue}")
    public void handle(PaymentRequestMessage requestMessage) {
        log.info("клиент прислал данные на создание платежа: request={}", requestMessage);
        var paymentMoney = requestMessage.money();

        var payment = paymentService.createPayment(
                requestMessage.orderId(),
                paymentMoney.amount(),
                paymentMoney.currency());

        // имитация успешной оплаты
        var updatedPayment = paymentService.updatePaymentStatus(payment.getId(), PaymentStatus.PAID);

        var response = PaymentResponseMessage.forStatusUpdated(updatedPayment);

        rabbitTemplate.convertAndSend(
                props.exchange(),
                props.paymentStatus().routingKey(),
                response);
        log.info("платеж был полностью обработан и зафинален");
    }
}
