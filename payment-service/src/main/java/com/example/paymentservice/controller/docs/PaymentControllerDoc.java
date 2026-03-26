package com.example.paymentservice.controller.docs;

import com.example.paymentservice.dto.request.CreatePaymentRequest;
import com.example.paymentservice.dto.request.UpdatePaymentRequest;
import com.example.paymentservice.dto.response.PaymentListResponse;
import com.example.paymentservice.dto.response.PaymentResponse;
import com.example.paymentservice.dto.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.util.UUID;

public interface PaymentControllerDoc {

    @Operation(
            summary = "Создать платёж",
            description = "Создаёт новый платёж для указанного заказа",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Платёж успешно создан")
            }
    )
    void create(CreatePaymentRequest request);

    @Operation(
            summary = "Получить все платежи",
            description = "Возвращает список всех платежей",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список платежей",
                            content = @Content(schema = @Schema(implementation = PaymentListResponse.class)))
            }
    )
    PaymentListResponse getAll();

    @Operation(
            summary = "Получить платёж по ID",
            description = "Возвращает данные платежа по его уникальному идентификатору",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Платёж найден",
                            content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Платёж не найден",
                            content = @Content(schema = @Schema(implementation = Response.class)))
            }
    )
    PaymentResponse getById(@Parameter(description = "ID платежа")
                            UUID id);

    @Operation(
            summary = "Обновить платёж",
            description = "Обновляет существующий платёж по его ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Платёж успешно обновлён"),
                    @ApiResponse(responseCode = "404", description = "Платёж не найден",
                            content = @Content(schema = @Schema(implementation = Response.class)))
            }
    )
    void update(@Parameter(description = "ID платежа")
                UUID id,
                UpdatePaymentRequest request);

    @Operation(
            summary = "Удалить платёж",
            description = "Удаляет платёж по его уникальному идентификатору",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Платёж успешно удалён"),
                    @ApiResponse(responseCode = "404", description = "Платёж не найден",
                            content = @Content(schema = @Schema(implementation = Response.class)))
            }
    )
    void delete(@Parameter(description = "ID платежа")
                UUID id);
}
