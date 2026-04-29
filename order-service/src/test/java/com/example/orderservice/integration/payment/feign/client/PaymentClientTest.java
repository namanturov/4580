package com.example.orderservice.integration.payment.feign.client;

import com.example.orderservice.exception.EntityNotFoundException;
import com.example.orderservice.exception.ExternalIntegrationServiceException;
import com.example.orderservice.integration.payment.enums.CurrencyType;
import com.example.orderservice.integration.payment.feign.dto.request.CreatePaymentRequest;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

import java.math.BigDecimal;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

@SpringBootTest
@ActiveProfiles("test")
@EnableWireMock(
        @ConfigureWireMock(
                name = "payment-service-mock",
                port = 5555,
                baseUrlProperties = "http://localhost",
                filesUnderClasspath = "wiremock"
        )
)
class PaymentClientTest {

    @Autowired
    private PaymentClient paymentClient;

    //    все тесты прошли.
    @Test
    void testCreatePaymentSuccess() {
        var idempotencyKey = UUID.randomUUID();
        var orderId = UUID.fromString("1131912d-67c2-4ec8-b4d3-61bff77f6f73");

        var req = CreatePaymentRequest.of(orderId, BigDecimal.TEN, CurrencyType.KGS);

        paymentClient.create(idempotencyKey, req);

        WireMock.verify(postRequestedFor(urlEqualTo("/payments")));
    }

    @Test
    void testCreatePaymentNotFound() {
        var idempotencyKey = UUID.randomUUID();
        var orderId = UUID.fromString("69d0f3ad-d7f8-8329-a327-ccdaa44d2d2d");

        var req = CreatePaymentRequest.of(orderId, BigDecimal.TEN, CurrencyType.KGS);

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            paymentClient.create(idempotencyKey, req);
        });

        WireMock.verify(postRequestedFor(urlEqualTo("/payments")));
    }

    @Test
    void testCreatePaymentBadRequest() {
        var idempotencyKey = UUID.randomUUID();
        var orderId = UUID.fromString("efe62388-10af-4094-b27e-b3a92f7cb887");

        var req = CreatePaymentRequest.of(orderId, BigDecimal.TEN, CurrencyType.KGS);

        Assertions.assertThrows(ExternalIntegrationServiceException.class, () -> {
            paymentClient.create(idempotencyKey, req);
        });

        WireMock.verify(postRequestedFor(urlEqualTo("/payments")));
    }
}