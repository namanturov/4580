package com.example.orderservice.integration.client;

import com.example.orderservice.config.feign.PaymentClientConfig;
import com.example.orderservice.dto.business.PaymentHttpHeader;
import com.example.orderservice.exception.RateLimitExceededException;
import com.example.orderservice.exception.ServiceUnavailableException;
import com.example.orderservice.exception.TooManyConcurrentRequestsException;
import com.example.orderservice.integration.dto.request.CreatePaymentRequest;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
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
    @CircuitBreaker(name = "paymentClient", fallbackMethod = "createFallbackOnCircuitBreaker")
    @RateLimiter(name = "paymentClient", fallbackMethod = "createFallbackOnRateLimiter")
    @Bulkhead(name = "paymentClient", fallbackMethod = "createFallbackOnBulkhead")
    @Retry(name = "paymentClient")//и тестируя понял что для него и не нужен fallbackMethod.
    void create(@RequestHeader(PaymentHttpHeader.IDEMPOTENCY)
                UUID idempotencyKey,
                @RequestBody
                CreatePaymentRequest request);

    //ну по идеи можно было на некоторые redirect на чет другие сервисы. Аля с кеша или тип того, но было лень думать,
    //потому чисто exceptions выкидываю. Ну а так дааа я пон что можно туда сюда делать.

    default void createFallbackOnBulkhead(UUID idempotencyKey,
                                          CreatePaymentRequest request,
                                          BulkheadFullException ignored) {
        throw new TooManyConcurrentRequestsException("Слишком много параллельных запросов к payment-service, попробуй позже");
    }

    default void createFallbackOnRateLimiter(UUID idempotencyKey,
                                             CreatePaymentRequest request,
                                             RequestNotPermitted ignored) {
        throw new RateLimitExceededException("Слишком много запросов к payment-service, попробуй позже");
    }

    default void createFallbackOnCircuitBreaker(UUID idempotencyKey,
                                                CreatePaymentRequest request,
                                                CallNotPermittedException ignored) {
        throw new ServiceUnavailableException("Payment-service временно недоступен, попробуй позже");
    }
}
