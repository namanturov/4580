package com.example.deliveryservice.controller;

import com.example.deliveryservice.controller.docs.DeliveryControllerDoc;
import com.example.deliveryservice.dto.request.UpdateDeliveryRequest;
import com.example.deliveryservice.dto.response.DeliveryListResponse;
import com.example.deliveryservice.dto.response.DeliveryResponse;
import com.example.deliveryservice.exception.ServiceUnavailableException;
import com.example.deliveryservice.service.DeliveryService;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/deliveries")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeliverController implements DeliveryControllerDoc {

    DeliveryService deliveryService;
    ModelMapper modelMapper;

    @Override
    @GetMapping
    @CircuitBreaker(name = "deliveryController", fallbackMethod = "getAllFallbackOnCB")
    public DeliveryListResponse getAll() {
        var deliveryList = deliveryService.getAllDeliveries();
        var deliveryResponseList = deliveryList.stream()
                .map(delivery -> modelMapper.map(delivery, DeliveryResponse.class))
                .toList();

        return DeliveryListResponse.of(deliveryResponseList);
    }

    private DeliveryListResponse getAllFallbackOnCB(CallNotPermittedException ignored) {
        return DeliveryListResponse.of(List.of());
    }

    @Override
    @GetMapping("/{id}")
    @CircuitBreaker(name = "deliveryController", fallbackMethod = "getByIdFallbackOnCB")
    public DeliveryResponse getById(@PathVariable
                                    UUID id) {
        var delivery = deliveryService.getDeliveryById(id);
        return modelMapper.map(delivery, DeliveryResponse.class);
    }

    private DeliveryResponse getByIdFallbackOnCB(UUID id, CallNotPermittedException ignored) {
        return new DeliveryResponse();
    }

    @Override
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CircuitBreaker(name = "deliveryController", fallbackMethod = "updateFallbackOnCB")
    public void update(@PathVariable
                       UUID id,
                       @RequestBody
                       UpdateDeliveryRequest request) {
        deliveryService.updateDelivery(id, request);
    }

    private void updateFallbackOnCB(UUID id, UpdateDeliveryRequest request, CallNotPermittedException ignored) {
        throw new ServiceUnavailableException("Пока обновление delivery не пашет, братец");
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CircuitBreaker(name = "deliveryController", fallbackMethod = "deleteFallbackOnCB")
    public void delete(@PathVariable
                       UUID id) {
        deliveryService.deleteDelivery(id);
    }

    private void deleteFallbackOnCB(UUID id, CallNotPermittedException ignored) {
        throw new ServiceUnavailableException("Удалить delivery сейчас нельзя, эк");
    }
}