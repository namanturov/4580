package com.example.orderservice.infrastructure.kafka.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "integration.kafka.topics")
public record KafkaTopicsProperties(
        OrderTopics order,
        DeliveryTopics delivery
) {
    public record OrderTopics(String completed) {
    }

    public record DeliveryTopics(String created) {
    }
}
