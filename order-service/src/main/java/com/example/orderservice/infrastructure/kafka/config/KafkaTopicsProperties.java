package com.example.orderservice.infrastructure.kafka.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "integration.kafka.topics")
public record KafkaTopicsProperties(
        OrderTopics order,
        PaymentTopics payment,
        DeliveryTopics delivery,
        SagaTopics saga
) {
    public record OrderTopics(
            String cancel,
            String approve) {
    }

    public record PaymentTopics(
            String create,
            String refund) {
    }

    public record DeliveryTopics(String create) {
    }

    public record SagaTopics(String orderCreationStatus) {
    }
}
