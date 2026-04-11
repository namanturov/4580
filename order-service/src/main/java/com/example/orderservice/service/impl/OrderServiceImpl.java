package com.example.orderservice.service.impl;

import com.example.orderservice.entity.Order;
import com.example.orderservice.enums.OrderStatus;
import com.example.orderservice.integration.payment.enums.CurrencyType;
import com.example.orderservice.integration.payment.enums.PaymentStatus;
import com.example.orderservice.integration.payment.feign.client.PaymentClient;
import com.example.orderservice.integration.payment.feign.dto.request.CreatePaymentRequest;
import com.example.orderservice.integration.payment.rabbitmq.dto.request.PaymentRequestMessage;
import com.example.orderservice.integration.payment.rabbitmq.producer.PaymentProducer;
import com.example.orderservice.repository.manager.OrderManager;
import com.example.orderservice.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderServiceImpl implements OrderService {

    PaymentProducer paymentProducer;
    PaymentClient paymentClient;
    OrderManager orderManager;

    @Override
    public void createOrder(String customerName) {
        var order = new Order()
                .setCustomerName(customerName)
                .setStatus(OrderStatus.CREATED);
        var createdOrder = orderManager.save(order);
        var reqMessage = PaymentRequestMessage.forCreate(createdOrder);
        paymentProducer.sendCreatePayment(reqMessage);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderManager.getAll();
    }

    @Override
    public Order getOrderById(UUID id) {
        return orderManager.getById(id);
    }

    @Override
    public void updateOrder(UUID id, Order order, UUID idempotencyKey) {
        var existOrder = orderManager.getById(id);
        var status = order.getStatus();
        existOrder.setPrevStatus(existOrder.getStatus())
                .setStatus(order.getStatus())
                .setCustomerName(order.getCustomerName());
        if (status == OrderStatus.SUCCESS
                || (status == OrderStatus.FAILED && existOrder.getPrevStatus() == OrderStatus.SUCCESS)) {
            BigDecimal fixedOrderPrice = BigDecimal.valueOf(4580.02);
            var createPaymentReq = CreatePaymentRequest.of(id, fixedOrderPrice, CurrencyType.KGS);
            paymentClient.create(idempotencyKey, createPaymentReq);
        }
    }

    @Override
    public void updateOrderStatus(UUID orderId, PaymentStatus paymentStatus) {
        var order = orderManager.getById(orderId);
        switch (paymentStatus) {
            case ERROR -> {
                order.setPrevStatus(order.getStatus());
                order.setStatus(OrderStatus.FAILED);
            }
            case PAID -> {
                order.setPrevStatus(order.getStatus());
                order.setStatus(OrderStatus.SUCCESS);
            }
            default -> log.error("Шо то не так начала передавать статусы платежей. Нужно поговорить с ними");
        }
        orderManager.save(order);
        log.info("заказ был зафинален по статусу поговорим с платежным ордером");
    }

    @Override
    public void deleteOrder(UUID id) {
        var order = orderManager.getById(id);

        orderManager.delete(order);
    }
}
