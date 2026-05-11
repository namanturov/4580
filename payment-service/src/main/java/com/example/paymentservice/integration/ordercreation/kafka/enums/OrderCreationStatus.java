package com.example.paymentservice.integration.ordercreation.kafka.enums;

public enum OrderCreationStatus {
    ORDER_CREATED,
    PAYMENT_CONFIRMED,
    PAYMENT_FAILED,
    DELIVERY_COMPLETED,
    DELIVERY_FAILED,
    CANCEL
}
