package com.example.paymentservice.controller;

import com.example.paymentservice.controller.docs.PaymentControllerDoc;
import com.example.paymentservice.dto.request.CreatePaymentRequest;
import com.example.paymentservice.dto.request.UpdatePaymentRequest;
import com.example.paymentservice.dto.response.PaymentListResponse;
import com.example.paymentservice.dto.response.PaymentResponse;
import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.exception.ServiceUnavailableException;
import com.example.paymentservice.service.PaymentService;
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
@RequestMapping("/payments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController implements PaymentControllerDoc {

    PaymentService paymentService;
    ModelMapper modelMapper;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "paymentController", fallbackMethod = "createFallbackOnCB")
    public PaymentResponse create(@RequestBody
                                  CreatePaymentRequest request) {
        var money = request.getMoney();
        var savedPayment = paymentService.createPayment(
                request.getOrderId(),
                money.getAmount(),
                money.getCurrency());
        return modelMapper.map(savedPayment, PaymentResponse.class);
    }

    private void createFallbackOnCB(CreatePaymentRequest request, CallNotPermittedException ignored) {
        throw new ServiceUnavailableException("Пока создать payment нельзя, братан");
    }

    @Override
    @GetMapping
    @CircuitBreaker(name = "paymentController", fallbackMethod = "getAllFallbackOnCB")
    public PaymentListResponse getAll() {
        var paymentList = paymentService.getAllPayments();
        var paymentResponseList = paymentList.stream()
                .map(payment -> modelMapper.map(payment, PaymentResponse.class))
                .toList();

        return PaymentListResponse.of(paymentResponseList);
    }

    private PaymentListResponse getAllFallbackOnCB(CallNotPermittedException ignored) {
        return PaymentListResponse.of(List.of());
    }

    @Override
    @GetMapping("/{id}")
    @CircuitBreaker(name = "paymentController", fallbackMethod = "getByIdFallbackOnCB")
    public PaymentResponse getById(@PathVariable
                                   UUID id) {
        var payment = paymentService.getPaymentById(id);
        return modelMapper.map(payment, PaymentResponse.class);
    }

    private PaymentResponse getByIdFallbackOnCB(UUID id, CallNotPermittedException ignored) {
        return new PaymentResponse();
    }

    @Override
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CircuitBreaker(name = "paymentController", fallbackMethod = "updateFallbackOnCB")
    public void update(@PathVariable
                       UUID id,
                       @RequestBody
                       UpdatePaymentRequest request) {

        var payment = modelMapper.map(request, Payment.class);
        paymentService.updatePayment(id, payment);
    }

    private void updateFallbackOnCB(UUID id, UpdatePaymentRequest request, CallNotPermittedException ignored) {
        throw new ServiceUnavailableException("Пока обновление payment не пашет, братец");
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CircuitBreaker(name = "paymentController", fallbackMethod = "deleteFallbackOnCB")
    public void delete(@PathVariable
                       UUID id) {
        paymentService.deletePayment(id);
    }

    private void deleteFallbackOnCB(UUID id, CallNotPermittedException ignored) {
        throw new ServiceUnavailableException("Удалить payment сейчас нельзя, эк");
    }
}