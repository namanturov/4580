package com.example.orderservice.controller.docs;

import com.example.orderservice.dto.request.CreateOrderRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

public interface OrderControllerDoc {

    @Operation(
            summary = "Создать заказ",
            description = "Создаёт новый заказ с указанными параметрами",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Заказ успешно создан")
            }
    )
    void create(CreateOrderRequest request);
}