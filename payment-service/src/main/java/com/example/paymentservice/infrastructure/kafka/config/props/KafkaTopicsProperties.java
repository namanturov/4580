package com.example.paymentservice.infrastructure.kafka.config.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "integration.kafka.topics")
public record KafkaTopicsProperties(
        OrderTopics order
) {
    public record OrderTopics(String creationStatus) {
    }
}
