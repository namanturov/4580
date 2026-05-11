package com.example.deliveryservice.service;

import com.example.deliveryservice.entity.Delivery;

import java.util.List;
import java.util.UUID;

public interface DeliveryService {
    List<Delivery> getAllDeliveries();

    Delivery getDeliveryById(UUID id);

    void deleteDelivery(UUID orderId);

    void createDelivery(UUID orderId);
}
