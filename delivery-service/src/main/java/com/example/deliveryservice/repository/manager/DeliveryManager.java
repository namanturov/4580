package com.example.deliveryservice.repository.manager;

import com.example.deliveryservice.entity.Delivery;
import com.example.deliveryservice.exception.EntityNotFoundException;
import com.example.deliveryservice.repository.DeliveryRepository;
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
public class DeliveryManager {

    DeliveryRepository deliveryRepository;

    @Transactional
    public Delivery save(Delivery delivery) {
        return deliveryRepository.save(delivery);
    }

    public List<Delivery> getAll() {
        return deliveryRepository.findAll();
    }

    public Delivery getById(UUID id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() -> {
                    var errorMessage = String.format("данная доставка не была найдена по id - %s", id);
                    log.error(errorMessage);
                    return new EntityNotFoundException(errorMessage);
                });
    }

    @Transactional
    public void delete(Delivery delivery) {
        deliveryRepository.delete(delivery);
    }

    public Delivery getByOrderId(UUID orderId) {
        return deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> {
                    var errorMessage = String.format("данная доставка не была найдена по order-id - %s", orderId);
                    log.error(errorMessage);
                    return new EntityNotFoundException(errorMessage);
                });
    }
}
