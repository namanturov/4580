package com.example.paymentservice.integration.ordercreation.listener;

import com.example.paymentservice.enums.CurrencyType;
import com.example.paymentservice.integration.ordercreation.dto.payment.PaymentCreateCommand;
import com.example.paymentservice.service.PaymentService;
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
public class PaymentCreateCommandListener {

    PaymentService paymentService;
    JsonMapper jsonMapper;

    @KafkaListener(
            topics = "${integration.kafka.topics.payment.create}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void handle(String message,
                       Acknowledgment ack) {
        try {
            var event = jsonMapper.readValue(message, PaymentCreateCommand.class);

            log.info("Платеж на создание по заявке ({})", event.orderId());
            paymentService.createPayment(event.orderId(), event.price(), CurrencyType.valueOf(event.currencyType()));
            //предположим что уже на этом этапе дальше как то он уже сел.
            log.info("Платеж по заявке создан и обработан");

            ack.acknowledge();
        } catch (Exception e) {
            log.error("Произошла ошибка во время обработки event: {}", e.getMessage(), e);
        }
    }
}