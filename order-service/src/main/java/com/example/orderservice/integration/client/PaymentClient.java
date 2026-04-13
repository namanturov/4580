package com.example.orderservice.integration.client;

import com.example.orderservice.config.feign.PaymentClientConfig;
import com.example.orderservice.integration.dto.request.CreatePaymentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "payment-client",
        url = "${clients.payment.url}",
        configuration = PaymentClientConfig.class
)
public interface PaymentClient {
    @PostMapping
    void create(CreatePaymentRequest request);
}
