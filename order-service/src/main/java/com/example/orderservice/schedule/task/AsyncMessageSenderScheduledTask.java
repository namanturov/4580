package com.example.orderservice.schedule.task;

import com.example.orderservice.schedule.processor.AsyncMessageSenderProcessor;
import com.example.orderservice.service.AsyncMessageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AsyncMessageSenderScheduledTask {

    AsyncMessageService asyncMessageService;
    AsyncMessageSenderProcessor processor;

    @Scheduled(fixedDelay = 5_000)
    public void sendOutboxMessages() {
        var messages = asyncMessageService.getUnsentOutboxMessages(50);
        messages.forEach(processor::sendMessage);
    }
}
