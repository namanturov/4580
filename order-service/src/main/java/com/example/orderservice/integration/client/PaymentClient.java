package com.example.orderservice.integration.client;

import com.example.orderservice.config.feign.PaymentClientConfig;
import com.example.orderservice.dto.business.PaymentHttpHeader;
import com.example.orderservice.integration.dto.request.CreatePaymentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(
        name = "payment-client",
        url = "${clients.payment.url}",
        configuration = PaymentClientConfig.class
)
public interface PaymentClient {
    @PostMapping
    void create(@RequestHeader(PaymentHttpHeader.IDEMPOTENCY)
                UUID idempotencyKey,
                @RequestBody
                CreatePaymentRequest request);
}
