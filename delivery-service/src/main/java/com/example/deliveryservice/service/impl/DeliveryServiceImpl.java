package com.example.deliveryservice.service.impl;

import com.example.deliveryservice.dto.business.Delivery;
import com.example.deliveryservice.entity.AddressEntity;
import com.example.deliveryservice.entity.DeliveryEntity;
import com.example.deliveryservice.enums.DeliveryStatus;
import com.example.deliveryservice.repository.manager.DeliveryManager;
import com.example.deliveryservice.service.DeliveryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeliveryServiceImpl implements DeliveryService {

    DeliveryManager deliveryManager;
    ModelMapper modelMapper;

    @Override
    public void create(Delivery delivery) {
        delivery.setStatus(DeliveryStatus.CREATED);
        var deliverEntity = modelMapper.map(delivery, DeliveryEntity.class);

        deliveryManager.save(deliverEntity);
    }

    @Override
    public List<Delivery> getAll() {
        var deliveryEntityList = deliveryManager.getAll();

        return deliveryEntityList.stream()
                .map(deliveryEntity -> modelMapper.map(deliveryEntity, Delivery.class))
                .toList();
    }

    @Override
    public Delivery getById(UUID id) {
        var deliveryEntity = deliveryManager.getById(id);

        return modelMapper.map(deliveryEntity, Delivery.class);
    }

    @Override
    public void update(UUID id, Delivery delivery) {
        var deliveryEntity = deliveryManager.getById(id);
        assignDeliveryEntityFull(deliveryEntity, delivery);

        deliveryManager.save(deliveryEntity);
    }

    private void assignDeliveryEntityFull(DeliveryEntity deliveryEntity, Delivery delivery) {
        deliveryEntity.setStatus(delivery.getStatus())
                .setOrderId(delivery.getOrderId());

        if (delivery.getAddress() != null) {
            var updateAddress = delivery.getAddress();
            deliveryEntity.setAddress(new AddressEntity()
                    .setCity(updateAddress.getCity())
                    .setStreet(updateAddress.getStreet())
                    .setHouse(updateAddress.getHouse()));
        }
    }

    @Override
    public void delete(UUID id) {
        var deliveryEntity = deliveryManager.getById(id);

        deliveryManager.delete(deliveryEntity);
    }
}
