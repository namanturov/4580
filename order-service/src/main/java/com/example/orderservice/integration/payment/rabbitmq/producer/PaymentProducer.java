package com.example.orderservice.integration.payment.rabbitmq.producer;

import com.example.orderservice.entity.Order;
import com.example.orderservice.integration.payment.rabbitmq.config.properties.PaymentRabbitMqProperties;
import com.example.orderservice.integration.payment.rabbitmq.dto.request.PaymentRequestMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentProducer {

    PaymentRabbitMqProperties props;
    RabbitTemplate rabbitTemplate;

    public void sendCreatePayment(Order order) {
        var reqMessage = PaymentRequestMessage.forCreate(order);
        rabbitTemplate.convertAndSend(
                props.exchange(),
                props.createPayment().routingKey(),
                reqMessage);
        log.info("заявка отправлена на оплату");
    }
}
