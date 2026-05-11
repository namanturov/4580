package com.example.paymentservice.integration.ordercreation.kafka.listener;

import com.example.paymentservice.enums.CurrencyType;
import com.example.paymentservice.integration.ordercreation.kafka.dto.OrderCreationStatusEvent;
import com.example.paymentservice.integration.ordercreation.kafka.enums.OrderCreationStatus;
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
public class OrderCreationStatusListener {

    PaymentService paymentService;
    JsonMapper jsonMapper;

    @KafkaListener(
            topics = "${integration.kafka.topics.order.creation-status}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void handle(String message,
                       Acknowledgment ack) {
        try {
            var event = jsonMapper.readValue(message, OrderCreationStatusEvent.class);

            if (event.status().equals(OrderCreationStatus.ORDER_CREATED)) {
                paymentService.createPayment(event.orderId(), event.price(), CurrencyType.KGS);
                //предположим что уже на этом этапе дальше как то он уже сел.
                log.info("Платеж по заявке ({}) создан и обработан", event.orderId());
            } else if (event.status().equals(OrderCreationStatus.CANCEL)) {
                paymentService.deletePayment(event.orderId());
                log.info("Платеж по заявке ({}) компенсация пошла поехала и done.", event.orderId());
            }

            ack.acknowledge();
        } catch (Exception e) {
            log.error("Произошла ошибка во время обработки event: {}", e.getMessage(), e);
        }
    }
}