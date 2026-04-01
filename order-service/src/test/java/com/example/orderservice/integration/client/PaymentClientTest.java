package com.example.orderservice.integration.client;

import com.example.orderservice.integration.dto.enums.CurrencyType;
import com.example.orderservice.integration.dto.request.CreatePaymentRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

import java.math.BigDecimal;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @Test
    void testCreateOrderSuccess() {
        var idempotencyKey = UUID.randomUUID();
        //ну предположим что заказ мы как то да и нашли...утаим как, но нашли.
        var orderId = UUID.randomUUID();
        var req = CreatePaymentRequest.of(orderId, BigDecimal.TEN, CurrencyType.KGS);
        ResponseEntity<Void> response = paymentClient.create(idempotencyKey, req);

        verify(postRequestedFor(urlEqualTo("/payments")));
        assertEquals(201, response.getStatusCode().value());
    }
}