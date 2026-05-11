package com.example.orderservice.saga.dto.orchestrator.enums;

public enum OrderCreationStatus {
    ORDER_CREATED,
    PAYMENT_CONFIRMED,
    PAYMENT_FAILED,
    DELIVERY_COMPLETED,
    DELIVERY_FAILED
}
