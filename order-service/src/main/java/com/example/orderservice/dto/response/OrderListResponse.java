package com.example.orderservice.dto.response;

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
public class OrderListResponse {
    List<OrderResponse> orders;

    public static OrderListResponse of(List<OrderResponse> orderResponseList) {
        return new OrderListResponse()
                .setOrders(orderResponseList);
    }
}
