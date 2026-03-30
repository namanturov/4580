package com.example.orderservice.controller;

import com.example.orderservice.controller.docs.OrderControllerDoc;
import com.example.orderservice.dto.business.Order;
import com.example.orderservice.dto.business.PaymentHttpHeader;
import com.example.orderservice.dto.request.CreateOrderRequest;
import com.example.orderservice.dto.request.UpdateOrderRequest;
import com.example.orderservice.dto.response.OrderListResponse;
import com.example.orderservice.dto.response.OrderResponse;
import com.example.orderservice.exception.ServiceUnavailableException;
import com.example.orderservice.service.OrderService;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController implements OrderControllerDoc {

    OrderService orderService;
    ModelMapper modelMapper;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "orderController", fallbackMethod = "createFallbackOnCB")
    public void create(@RequestBody
                       CreateOrderRequest request) {
        var order = modelMapper.map(request, Order.class);
        orderService.create(order);
    }

    private void createFallbackOnCB(CreateOrderRequest request, CallNotPermittedException ignored) {
        throw new ServiceUnavailableException("Пока создать нельзя, братан");
    }

    @Override
    @GetMapping
    @CircuitBreaker(name = "orderController", fallbackMethod = "getAllFallbackOnCB")
    public OrderListResponse getAll() {
        var orderList = orderService.getAll();
        var orderResponseList = orderList.stream()
                .map(order -> modelMapper.map(order, OrderResponse.class))
                .toList();

        return OrderListResponse.of(orderResponseList);
    }

    private OrderListResponse getAllFallbackOnCB(CallNotPermittedException ignored) {
        return OrderListResponse.of(List.of());
    }

    @Override
    @GetMapping("/{id}")
    @CircuitBreaker(name = "orderController", fallbackMethod = "getByIdFallbackOnCB")
    public OrderResponse getById(@PathVariable
                                 UUID id) {
        var order = orderService.getById(id);
        return modelMapper.map(order, OrderResponse.class);
    }

    private OrderResponse getByIdFallbackOnCB(UUID id, CallNotPermittedException ignored) {
        //ну аля с кеша если будет можно отдать как будто бы как будто
        return new OrderResponse();
    }

    @Override
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CircuitBreaker(name = "orderController", fallbackMethod = "updateFallbackOnCB")
    public void update(@RequestHeader(PaymentHttpHeader.IDEMPOTENCY)
                       UUID idempotencyKey,
                       @PathVariable
                       UUID id,
                       @RequestBody
                       UpdateOrderRequest request) {

        var order = modelMapper.map(request, Order.class);
        orderService.update(id, order, idempotencyKey);
    }

    private void updateFallbackOnCB(UUID idempotencyKey, UUID id, UpdateOrderRequest request, CallNotPermittedException ignored) {
        throw new ServiceUnavailableException("Пока обновление не пашет, братец");
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CircuitBreaker(name = "orderController", fallbackMethod = "deleteFallbackOnCB")
    public void delete(@PathVariable UUID id) {
        orderService.delete(id);
    }

    private void deleteFallbackOnCB(UUID id, CallNotPermittedException ignored) {
        throw new ServiceUnavailableException("Удалить не получится пока что, эк");
    }
}
