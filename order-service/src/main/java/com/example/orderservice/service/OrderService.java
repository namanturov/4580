package com.example.orderservice.service;


import com.example.orderservice.entity.Order;
import com.example.orderservice.integration.payment.enums.PaymentStatus;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    void createOrder(String customerName);

    List<Order> getAllOrders();

    Order getOrderById(UUID id);

    void updateOrder(UUID id, Order order, UUID idempotencyKey);

    void updateOrderStatus(UUID orderId, PaymentStatus paymentStatus);

    void deleteOrder(UUID id);
}
