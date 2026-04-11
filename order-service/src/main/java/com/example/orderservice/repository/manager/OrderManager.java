package com.example.orderservice.repository.manager;

import com.example.orderservice.entity.Order;
import com.example.orderservice.exception.EntityNotFoundException;
import com.example.orderservice.repository.OrderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderManager {

    OrderRepository orderRepository;

    @Transactional
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    public Order getById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> {
                    var errorMessage = String.format("данная доставка не была найдена по id - %s", id);
                    log.error(errorMessage);
                    return new EntityNotFoundException(errorMessage);
                });
    }

    @Transactional
    public void delete(Order order) {
        orderRepository.delete(order);
    }
}
