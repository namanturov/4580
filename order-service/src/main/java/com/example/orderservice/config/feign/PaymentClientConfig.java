package com.example.orderservice.config.feign;

import com.example.orderservice.integration.handler.PaymentClientErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentClientConfig {

    @Bean
    public PaymentClientErrorDecoder paymentClientErrorDecoder(){
        return new PaymentClientErrorDecoder();
    }
}
