package com.example.orderservice.service;


import com.example.orderservice.entity.Order;
import com.example.orderservice.enums.OrderStatus;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    void createOrder(String customerName);

    List<Order> getAllOrders();

    Order getOrderById(UUID id);

    void updateOrderStatus(UUID orderId, OrderStatus status);

    void cancelOrder(UUID orderId);
}
