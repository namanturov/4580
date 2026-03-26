package com.example.deliveryservice.controller.docs;

import com.example.deliveryservice.dto.request.CreateDeliveryRequest;
import com.example.deliveryservice.dto.request.UpdateDeliveryRequest;
import com.example.deliveryservice.dto.response.DeliveryListResponse;
import com.example.deliveryservice.dto.response.DeliveryResponse;
import com.example.deliveryservice.dto.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.UUID;

@Tag(name = "Deliveries", description = "Операции с доставками")
public interface DeliveryControllerDoc {

    @Operation(
            summary = "Создать доставку",
            description = "Создаёт новую доставку с указанными параметрами",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Доставка успешно создана"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса",
                            content = @Content(schema = @Schema(implementation = Response.class)))
            }
    )
    void create(CreateDeliveryRequest request);

    @Operation(
            summary = "Получить все доставки",
            description = "Возвращает список всех доставок",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список доставок",
                            content = @Content(schema = @Schema(implementation = DeliveryListResponse.class)))
            }
    )
    DeliveryListResponse getAll();

    @Operation(
            summary = "Получить доставку по ID",
            description = "Возвращает данные доставки по её уникальному идентификатору",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Доставка найдена",
                            content = @Content(schema = @Schema(implementation = DeliveryResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Доставка не найдена",
                            content = @Content(schema = @Schema(implementation = Response.class)))
            }
    )
    DeliveryResponse getById(@Parameter(description = "ID доставки")
                             UUID id);

    @Operation(
            summary = "Обновить доставку",
            description = "Обновляет существующую доставку по её ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Доставка успешно обновлена"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса",
                            content = @Content(schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "404", description = "Доставка не найдена",
                            content = @Content(schema = @Schema(implementation = Response.class)))
            }
    )
    void update(@Parameter(description = "ID доставки")
                UUID id,
                UpdateDeliveryRequest request);

    @Operation(
            summary = "Удалить доставку",
            description = "Удаляет доставку по её уникальному идентификатору",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Доставка успешно удалена"),
                    @ApiResponse(responseCode = "404", description = "Доставка не найдена",
                            content = @Content(schema = @Schema(implementation = Response.class)))
            }
    )
    void delete(@Parameter(description = "ID доставки") UUID id);
}
