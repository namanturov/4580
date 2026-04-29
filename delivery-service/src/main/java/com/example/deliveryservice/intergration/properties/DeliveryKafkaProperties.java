package com.example.deliveryservice.intergration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "integration.kafka.delivery")
public record DeliveryKafkaProperties(
        Topics topics
) {
    public record Topics(
            String created
    ) {
    }
}
