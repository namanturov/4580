package com.example.orderservice.schedule.processor;

import com.example.orderservice.entity.async.AsyncMessage;
import com.example.orderservice.exception.SendingAsyncMessageException;
import com.example.orderservice.service.AsyncMessageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AsyncMessageSenderProcessor {

    KafkaTemplate<String, Object> kafkaTemplate;
    AsyncMessageService asyncMessageService;

    @Transactional
    public void sendMessage(AsyncMessage message) {
        try {
            log.info("Отправка сообщения в Kafka. topic={}, key={}, messageId={}",
                    message.getMessageId().getTopic(),
                    message.getMessageId().getId(),
                    message.getMessageId());

            kafkaTemplate.send(
                            message.getMessageId().getTopic(),
                            message.getMessageId().getId().toString(),
                            message.getPayload())
                    .exceptionally(e -> {
                        throw new SendingAsyncMessageException("Ошибка при отправке сообщения '%s'".formatted(message));
                    })
                    .get();

            message.markAsSent();
            asyncMessageService.saveMessage(message);
            log.info("Сообщение помечено как SENT и сохранено. messageId={}", message.getMessageId());
        } catch (Exception e) {
            throw new SendingAsyncMessageException("Ошибка при отправке сообщения '%s'".formatted(message));
        }
    }
}
