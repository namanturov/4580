package com.example.paymentservice.infrastructure.kafka.config;

import com.example.paymentservice.infrastructure.kafka.config.props.KafkaTopicsProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(KafkaTopicsProperties.class)
public class KafkaConfig {
}
