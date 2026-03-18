package com.example.deliveryservice.service;

import com.example.deliveryservice.dto.business.Delivery;

import java.util.List;
import java.util.UUID;

public interface DeliveryService {
    void create(Delivery delivery);

    List<Delivery> getAll();

    Delivery getById(UUID id);

    void update(UUID id, Delivery delivery);

    void delete(UUID id);
}
