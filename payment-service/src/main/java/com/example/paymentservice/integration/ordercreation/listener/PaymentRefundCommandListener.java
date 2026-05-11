package com.example.paymentservice.integration.ordercreation.listener;

import com.example.paymentservice.integration.ordercreation.dto.payment.PaymentRefundCommand;
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
public class PaymentRefundCommandListener {

    PaymentService paymentService;
    JsonMapper jsonMapper;

    @KafkaListener(
            topics = "${integration.kafka.topics.payment.refund}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void handle(String message,
                       Acknowledgment ack) {
        try {
            var event = jsonMapper.readValue(message, PaymentRefundCommand.class);

            log.info("Платеж на возврат средств клиенту по заявке ({})", event.orderId());
            paymentService.refundPayment(event.orderId());
            log.info("Средства по заявке корректно возвращен во владения клиента");

            ack.acknowledge();
        } catch (Exception e) {
            log.error("Произошла ошибка во время обработки event: {}", e.getMessage(), e);
        }
    }
}