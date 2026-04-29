package com.example.orderservice.integration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "integration.kafka.order")
public record OrderKafkaProperties(
        Topics topics
) {
    public record Topics(
            String completed
    ) {

    }
}
