package com.example.paymentservice.controller;

import com.example.paymentservice.controller.docs.PaymentControllerDoc;
import com.example.paymentservice.dto.business.Payment;
import com.example.paymentservice.dto.request.CreatePaymentRequest;
import com.example.paymentservice.dto.request.UpdatePaymentRequest;
import com.example.paymentservice.dto.response.PaymentListResponse;
import com.example.paymentservice.dto.response.PaymentResponse;
import com.example.paymentservice.service.PaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
    public void create(@RequestBody
                       CreatePaymentRequest request) {
        var payment = modelMapper.map(request, Payment.class);

        paymentService.create(payment);
    }

    @Override
    @GetMapping
    public PaymentListResponse getAll() {
        var paymentList = paymentService.getAll();
        var paymentResponseList = paymentList.stream()
                .map(payment -> modelMapper.map(payment, PaymentResponse.class))
                .toList();

        return PaymentListResponse.of(paymentResponseList);
    }

    @Override
    @GetMapping("/{id}")
    public PaymentResponse getById(@PathVariable
                                   UUID id) {
        var payment = paymentService.getById(id);

        return modelMapper.map(payment, PaymentResponse.class);
    }

    @Override
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable
                       UUID id,
                       @RequestBody
                       UpdatePaymentRequest request) {
        var payment = modelMapper.map(request, Payment.class);

        paymentService.update(id, payment);
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable
                       UUID id) {
        paymentService.delete(id);
    }
}