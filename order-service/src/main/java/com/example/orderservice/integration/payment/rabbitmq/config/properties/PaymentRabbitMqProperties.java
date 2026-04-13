package com.example.orderservice.integration.payment.rabbitmq.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "integration.rabbitmq.payment")
public record PaymentRabbitMqProperties(
        String exchange,

        Routing createPayment
) {
    public record Routing(
            String routingKey,
            String queue
    ) {
    }
}
