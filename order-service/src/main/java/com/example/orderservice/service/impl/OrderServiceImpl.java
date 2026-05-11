package com.example.orderservice.service.impl;

import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.async.AsyncMessage;
import com.example.orderservice.enums.OrderStatus;
import com.example.orderservice.infrastructure.kafka.config.KafkaTopicsProperties;
import com.example.orderservice.repository.manager.OrderManager;
import com.example.orderservice.saga.dto.orchestrator.OrderCreationStatusEvent;
import com.example.orderservice.saga.dto.orchestrator.enums.OrderCreationStatus;
import com.example.orderservice.service.AsyncMessageService;
import com.example.orderservice.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderServiceImpl implements OrderService {

    AsyncMessageService asyncMessageService;
    KafkaTopicsProperties kafkaTopicsProps;
    OrderManager orderManager;
    JsonMapper jsonMapper;

    @Override
    public void createOrder(String customerName) {
        var order = new Order()
                .setCustomerName(customerName)
                .setStatus(OrderStatus.CREATED);
        var createdOrder = orderManager.save(order);
        var event = OrderCreationStatusEvent.builder()
                .orderId(createdOrder.getId())
                .status(OrderCreationStatus.ORDER_CREATED)
                .build();
        saveEventInOutbox(event);
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
    public void updateOrderStatus(UUID orderId, OrderStatus status) {
        var order = orderManager.getById(orderId);
        order.setPrevStatus(order.getStatus());
        order.setStatus(status);
        orderManager.save(order);
    }

    @Override
    public void cancelOrder(UUID orderId) {
        var order = orderManager.getById(orderId);
        order.setStatus(OrderStatus.FAILED);
        orderManager.save(order);
    }

    private void saveEventInOutbox(OrderCreationStatusEvent event) {
        var payload = jsonMapper.writeValueAsString(event);

        var asyncMessage = AsyncMessage.createOutboxMessage(
                kafkaTopicsProps.saga().orderCreationStatus(),
                payload);

        asyncMessageService.saveMessage(asyncMessage);
        log.info("Создано и сохранено outbox-сообщение saga-order-creation orchestrator. orderId={}, topic={}",
                event.orderId(),
                asyncMessage.getMessageId().getTopic());
    }
}
