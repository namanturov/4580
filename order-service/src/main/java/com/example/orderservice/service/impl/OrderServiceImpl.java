package com.example.orderservice.service.impl;

import com.example.orderservice.dto.request.UpdateOrderRequest;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.async.AsyncMessage;
import com.example.orderservice.enums.OrderStatus;
import com.example.orderservice.integration.delivery.kafka.dto.request.OrderCompletedEvent;
import com.example.orderservice.integration.payment.enums.CurrencyType;
import com.example.orderservice.integration.payment.enums.PaymentStatus;
import com.example.orderservice.integration.payment.feign.client.PaymentClient;
import com.example.orderservice.integration.payment.feign.dto.request.CreatePaymentRequest;
import com.example.orderservice.integration.payment.rabbitmq.producer.PaymentProducer;
import com.example.orderservice.integration.properties.OrderKafkaProperties;
import com.example.orderservice.repository.manager.OrderManager;
import com.example.orderservice.service.AsyncMessageService;
import com.example.orderservice.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderServiceImpl implements OrderService {

    AsyncMessageService asyncMessageService;
    OrderKafkaProperties orderKafkaProps;
    PaymentProducer paymentProducer;
    PaymentClient paymentClient;
    OrderManager orderManager;
    JsonMapper jsonMapper;

    @Override
    public void createOrder(String customerName) {
        var order = new Order()
                .setCustomerName(customerName)
                .setStatus(OrderStatus.CREATED);
        var createdOrder = orderManager.save(order);
        paymentProducer.sendCreatePayment(createdOrder);
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
    public void updateOrder(UUID id, UpdateOrderRequest request, UUID idempotencyKey) {
        var existOrder = orderManager.getById(id);
        var status = request.getStatus();
        existOrder.setPrevStatus(existOrder.getStatus())
                .setStatus(request.getStatus())
                .setCustomerName(request.getCustomerName());
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
        log.info("заказ был зафинален по статусу поговорив с платежным ордером");
        createAndSaveOrderCompletedEvent(order);
    }

    private void createAndSaveOrderCompletedEvent(Order order) {
        var event = OrderCompletedEvent.builder()
                .orderId(order.getId())
                .build();

        var payload = jsonMapper.writeValueAsString(event);

        var asyncMessage = AsyncMessage.createOutboxMessage(
                orderKafkaProps.topics().completed(),
                payload);

        asyncMessageService.saveMessage(asyncMessage);
        log.info("Создано и сохранено outbox-сообщение OrderCompleted. orderId={}, topic={}",
                order.getId(),
                asyncMessage.getMessageId().getTopic());
    }

    @Override
    public void deleteOrder(UUID id) {
        var order = orderManager.getById(id);

        orderManager.delete(order);
    }
}
