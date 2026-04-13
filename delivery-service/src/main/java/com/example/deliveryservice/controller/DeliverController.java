package com.example.deliveryservice.controller;

import com.example.deliveryservice.controller.docs.DeliveryControllerDoc;
import com.example.deliveryservice.dto.business.Delivery;
import com.example.deliveryservice.dto.request.CreateDeliveryRequest;
import com.example.deliveryservice.dto.request.UpdateDeliveryRequest;
import com.example.deliveryservice.dto.response.DeliveryListResponse;
import com.example.deliveryservice.dto.response.DeliveryResponse;
import com.example.deliveryservice.service.DeliveryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/deliveries")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeliverController implements DeliveryControllerDoc {

    DeliveryService deliveryService;
    ModelMapper modelMapper;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody
                       CreateDeliveryRequest request) {
        var delivery = modelMapper.map(request, Delivery.class);

        deliveryService.create(delivery);
    }

    @Override
    @GetMapping
    public DeliveryListResponse getAll() {
        var deliveryList = deliveryService.getAll();
        var deliveryResponseList = deliveryList.stream()
                .map(delivery -> modelMapper.map(delivery, DeliveryResponse.class))
                .toList();

        return DeliveryListResponse.of(deliveryResponseList);
    }


    @Override
    @GetMapping("/{id}")
    public DeliveryResponse getById(@PathVariable
                                    UUID id) {
        var delivery = deliveryService.getById(id);

        return modelMapper.map(delivery, DeliveryResponse.class);
    }

    @Override
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable
                       UUID id,
                       @RequestBody
                       UpdateDeliveryRequest request) {
        var delivery = modelMapper.map(request, Delivery.class);

        deliveryService.update(id, delivery);
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable
                       UUID id) {
        deliveryService.delete(id);
    }
}
