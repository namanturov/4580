package com.example.orderservice.controller.docs;

import com.example.orderservice.infrastructure.header.Headers;
import com.example.orderservice.dto.request.CreateOrderRequest;
import com.example.orderservice.dto.request.UpdateOrderRequest;
import com.example.orderservice.dto.response.OrderListResponse;
import com.example.orderservice.dto.response.OrderResponse;
import com.example.orderservice.dto.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

public interface OrderControllerDoc {

    @Operation(
            summary = "Создать заказ",
            description = "Создаёт новый заказ с указанными параметрами",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Заказ успешно создан")
            }
    )
    void create(CreateOrderRequest request);

    @Operation(
            summary = "Получить все заказы",
            description = "Возвращает список всех заказов",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список заказов",
                            content = @Content(schema = @Schema(implementation = List.class)))
            }
    )
    OrderListResponse getAll();

    @Operation(
            summary = "Получить заказ по ID",
            description = "Возвращает данные заказа по его уникальному идентификатору",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Заказ найден",
                            content = @Content(schema = @Schema(implementation = OrderResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Заказ не найден",
                            content = @Content(schema = @Schema(implementation = Response.class)))
            }
    )
    OrderResponse getById(@Parameter(description = "ID заказа")
                          UUID id);

    @Operation(
            summary = "Обновить заказ",
            description = "Обновляет существующий заказ по его ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Заказ успешно обновлён"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса",
                            content = @Content(schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "404", description = "Заказ не найден",
                            content = @Content(schema = @Schema(implementation = Response.class)))
            }
    )
    void update(@RequestHeader(Headers.IDEMPOTENCY_KEY)
                UUID idempotencyKey,
                @Parameter(description = "ID заказа")
                UUID id,
                UpdateOrderRequest request);

    @Operation(
            summary = "Удалить заказ",
            description = "Удаляет заказ по его уникальному идентификатору",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Заказ успешно удалён"),
                    @ApiResponse(responseCode = "404", description = "Заказ не найден",
                            content = @Content(schema = @Schema(implementation = Response.class)))
            }
    )
    void delete(@Parameter(description = "ID заказа")
                UUID id);
}