package com.example.paymentservice.integration.order.rabbitmq.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "integration.rabbitmq.order")
public record OrderRabbitMqProperties(
        String exchange,

        Routing paymentStatus
) {
    public record Routing(
            String routingKey,
            String queue
    ) {
    }
}