package com.example.orderservice.entity.async;

import com.example.orderservice.enums.AsyncMessageStatus;
import com.example.orderservice.enums.AsyncMessageType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Persistable;

import java.time.OffsetDateTime;
import java.util.UUID;

@Table(name = "async_messages")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AsyncMessage implements Persistable<AsyncMessageId> {
    @EmbeddedId
    AsyncMessageId messageId;
    String headers;
    String payload;
    @Enumerated(EnumType.STRING)
    AsyncMessageType type;
    @Enumerated(EnumType.STRING)
    AsyncMessageStatus status;
    @CreationTimestamp
    OffsetDateTime createdAt;

    public static AsyncMessage createOutboxMessage(String topic, String payload) {
        return AsyncMessage.builder()
                .messageId(AsyncMessageId.builder()
                        .id(UUID.randomUUID())
                        .topic(topic)
                        .build())
                .payload(payload)
                .type(AsyncMessageType.OUTBOX)
                .status(AsyncMessageStatus.CREATED)
                .build();
    }

    @Override
    public @Nullable AsyncMessageId getId() {
        return messageId;
    }

    @Override
    public boolean isNew() {
        return createdAt == null;
    }

    public void markAsSent() {
        this.status = AsyncMessageStatus.SENT;
    }
}
