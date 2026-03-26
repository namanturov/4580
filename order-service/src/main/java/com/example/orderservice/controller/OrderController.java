package com.example.orderservice.controller;

import com.example.orderservice.controller.docs.OrderControllerDoc;
import com.example.orderservice.dto.business.Order;
import com.example.orderservice.dto.business.PaymentHttpHeader;
import com.example.orderservice.dto.request.CreateOrderRequest;
import com.example.orderservice.dto.request.UpdateOrderRequest;
import com.example.orderservice.dto.response.OrderListResponse;
import com.example.orderservice.dto.response.OrderResponse;
import com.example.orderservice.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
    public void create(@RequestBody
                       CreateOrderRequest request) {
        var order = modelMapper.map(request, Order.class);

        orderService.create(order);
    }

    @Override
    @GetMapping
    public OrderListResponse getAll() {
        var orderList = orderService.getAll();
        var orderResponseList = orderList.stream()
                .map(order -> modelMapper.map(order, OrderResponse.class))
                .toList();

        return OrderListResponse.of(orderResponseList);
    }

    @Override
    @GetMapping("/{id}")
    public OrderResponse getById(@PathVariable
                                 UUID id) {
        var order = orderService.getById(id);

        return modelMapper.map(order, OrderResponse.class);
    }

    @Override
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@RequestHeader(PaymentHttpHeader.IDEMPOTENCY)
                       UUID idempotencyKey,
                       @PathVariable
                       UUID id,
                       @RequestBody
                       UpdateOrderRequest request) {
        var order = modelMapper.map(request, Order.class);

        orderService.update(id, order, idempotencyKey);
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable
                       UUID id) {
        orderService.delete(id);
    }
}
