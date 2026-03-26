package com.example.paymentservice.entity;

import com.example.paymentservice.enums.IdempotencyStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Table(name = "idempotency_store")
@Entity
@Getter
@Setter
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IdempotencyStore {
    @Id
    @Column(name = "key_value")
    UUID key;
    String requestHash;
    @Enumerated(EnumType.STRING)
    IdempotencyStatus status;
    int responseStatus;
    @Lob
    String responseBody;
    @Column(nullable = false, updatable = false)
    Instant createdAt;
    @UpdateTimestamp
    Instant updatedAt;
    @Column(nullable = false, updatable = false)
    Instant expiresAt;
    @Version
    Long version;
}
