package com.example.orderservice.config.feign;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.example.orderservice.integration.*.feign")
public class FeignConfig {
}
