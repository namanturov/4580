package com.example.deliveryservice.infrastructure.kafka.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "integration.kafka.topics")
public record KafkaTopicsProperties(
        DeliveryTopics delivery
) {
    public record DeliveryTopics(String created) {
    }
}
