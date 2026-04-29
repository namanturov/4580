package com.example.orderservice.infrastructure.kafka.listener;

import com.example.orderservice.entity.async.AsyncMessage;
import com.example.orderservice.infrastructure.header.Headers;
import com.example.orderservice.service.AsyncMessageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.support.Acknowledgment;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class IdempotentKafkaListener<T> {

    AsyncMessageService asyncMessageService;
    JsonMapper jsonMapper;

    public void consume(ConsumerRecord<String, String> consumerRecord,
                        String message,
                        Acknowledgment ack) {
        Header idemKeyHeader = consumerRecord.headers().lastHeader(Headers.IDEMPOTENCY_KEY);
        if (idemKeyHeader == null) {
            log.error("Отсутствует idempotency key в заголовках сообщения. topic={}, partition={}, offset={}",
                    consumerRecord.topic(),
                    consumerRecord.partition(),
                    consumerRecord.offset());

            ack.acknowledge();
            return;
        }

        var idempotentKey = new String(idemKeyHeader.value(), StandardCharsets.UTF_8);
        if (!isValidUUID(idempotentKey)) {
            log.error("Некорректный формат idempotency key: {}. topic={}, partition={}, offset={}",
                    idempotentKey,
                    consumerRecord.topic(),
                    consumerRecord.partition(),
                    consumerRecord.offset());

            ack.acknowledge();
            return;
        }
        var asyncMessage = AsyncMessage.createInboxMessage(
                idempotentKey,
                consumerRecord.topic(),
                message);

        try {
            asyncMessageService.saveMessage(asyncMessage);
        } catch (DataIntegrityViolationException ex) {
            log.warn("Сообщение с таким idempotency key уже обработано, пропускаем. idempotencyKey={}, topic={}",
                    idempotentKey,
                    consumerRecord.topic());
            ack.acknowledge();
            return;
        }

        T event = jsonMapper.readValue(message, getPayloadClass());

        log.info("Получено сообщение для обработки. idempotencyKey={}, topic={}",
                idempotentKey,
                consumerRecord.topic());

        processConsumedMessage(event);
        ack.acknowledge();

        log.info("Сообщение успешно обработано. idempotencyKey={}, topic={}",
                idempotentKey,
                consumerRecord.topic());
    }

    protected abstract void processConsumedMessage(T event);

    protected abstract Class<T> getPayloadClass();


    private boolean isValidUUID(String value) {
        try {
            UUID.fromString(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
