package com.example.orderservice.controller;

import com.example.orderservice.controller.docs.OrderControllerDoc;
import com.example.orderservice.dto.request.CreateOrderRequest;
import com.example.orderservice.exception.ServiceUnavailableException;
import com.example.orderservice.service.OrderService;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController implements OrderControllerDoc {

    OrderService orderService;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "orderController", fallbackMethod = "createFallbackOnCB")
    public void create(@RequestBody
                       CreateOrderRequest request) {
        orderService.createOrder(request.getCustomerName());
    }

    private void createFallbackOnCB(CreateOrderRequest request, CallNotPermittedException ignored) {
        throw new ServiceUnavailableException("Пока создать нельзя, братан");
    }
}
