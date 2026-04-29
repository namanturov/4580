package com.example.deliveryservice.service;

import com.example.deliveryservice.dto.request.UpdateDeliveryRequest;
import com.example.deliveryservice.entity.Delivery;

import java.util.List;
import java.util.UUID;

public interface DeliveryService {
    List<Delivery> getAllDeliveries();

    Delivery getDeliveryById(UUID id);

    void updateDelivery(UUID id, UpdateDeliveryRequest request);

    void deleteDelivery(UUID id);

    Delivery createDelivery(UUID orderId);
}
