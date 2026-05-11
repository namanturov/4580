package com.example.paymentservice.infrastructure.kafka.config.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "integration.kafka.topics")
public record KafkaTopicsProperties(
        SagaTopics saga,
        PaymentTopics payment

) {
    public record SagaTopics(String orderCreationStatus) {
    }

    public record PaymentTopics(String create,
                                String refund) {
    }
}
