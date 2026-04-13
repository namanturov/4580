package com.example.orderservice.integration.payment.feign.config;

import com.example.orderservice.integration.payment.feign.handler.PaymentClientErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentClientConfig {

    @Bean
    public PaymentClientErrorDecoder paymentClientErrorDecoder(){
        return new PaymentClientErrorDecoder();
    }
}
