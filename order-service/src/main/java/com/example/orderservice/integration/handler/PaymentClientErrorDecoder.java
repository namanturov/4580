package com.example.orderservice.integration.handler;

import com.example.orderservice.exception.EntityNotFoundException;
import com.example.orderservice.exception.ExternalIntegrationServiceException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentClientErrorDecoder implements ErrorDecoder {
    ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {

        if (response.status() == 404) {
            return new EntityNotFoundException("Не можется найтись платежный ордер");
        }

        if (response.status() >= 400) {
            return new ExternalIntegrationServiceException("Сервис платежной ординатуры отвечает как то странно. "
                    + "Прошу ждать, мы уже чиним.");
        }

        return defaultDecoder.decode(methodKey, response);
    }
}
