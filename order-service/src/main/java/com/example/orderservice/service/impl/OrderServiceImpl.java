package com.example.orderservice.service.impl;

import com.example.orderservice.dto.business.Order;
import com.example.orderservice.entity.OrderEntity;
import com.example.orderservice.enums.OrderStatus;
import com.example.orderservice.integration.client.PaymentClient;
import com.example.orderservice.integration.dto.enums.CurrencyType;
import com.example.orderservice.integration.dto.request.CreatePaymentRequest;
import com.example.orderservice.repository.manager.OrderManager;
import com.example.orderservice.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderServiceImpl implements OrderService {

    PaymentClient paymentClient;
    OrderManager orderManager;
    ModelMapper modelMapper;

    @Override
    public void create(Order order) {
        order.setStatus(OrderStatus.CREATED);
        var orderEntity = modelMapper.map(order, OrderEntity.class);
        orderManager.save(orderEntity);
    }

    @Override
    public List<Order> getAll() {
        var orderEntityList = orderManager.getAll();

        return orderEntityList.stream()
                .map(orderEntity -> modelMapper.map(orderEntity, Order.class))
                .toList();
    }

    @Override
    public Order getById(UUID id) {
        var orderEntity = orderManager.getById(id);

        return modelMapper.map(orderEntity, Order.class);
    }

    @Override
    public void update(UUID id, Order order, UUID idempotencyKey) {
        var orderEntity = orderManager.getById(id);
        var status = order.getStatus();
        orderEntity.setPrevStatus(orderEntity.getStatus())
                .setStatus(order.getStatus())
                .setCustomerName(order.getCustomerName());
        if (status == OrderStatus.SUCCESS
                || (status == OrderStatus.FAILED && orderEntity.getPrevStatus() == OrderStatus.SUCCESS)) {
            BigDecimal fixedOrderPrice = BigDecimal.valueOf(4580.02);
            var createPaymentReq = CreatePaymentRequest.of(id, fixedOrderPrice, CurrencyType.KGS);
            paymentClient.create(idempotencyKey, createPaymentReq);
        }
    }

    @Override
    public void delete(UUID id) {
        var orderEntity = orderManager.getById(id);

        orderManager.delete(orderEntity);
    }
}
