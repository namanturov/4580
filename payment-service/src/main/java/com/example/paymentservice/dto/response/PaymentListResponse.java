package com.example.paymentservice.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentListResponse {
    List<PaymentResponse> payments;

    public static PaymentListResponse of(List<PaymentResponse> paymentResponseList) {
        return new PaymentListResponse()
                .setPayments(paymentResponseList);
    }
}
