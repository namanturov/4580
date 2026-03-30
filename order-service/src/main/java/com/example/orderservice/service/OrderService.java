package com.example.orderservice.service;

import com.example.orderservice.dto.business.Order;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    void create(Order order);

    List<Order> getAll();

    Order getById(UUID id);

    void update(UUID id, Order order, UUID idempotencyKey);

    void delete(UUID id);
}
