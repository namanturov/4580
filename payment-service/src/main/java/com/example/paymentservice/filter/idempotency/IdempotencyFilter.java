package com.example.paymentservice.filter.idempotency;

import com.example.paymentservice.enums.IdempotencyStatus;
import com.example.paymentservice.exception.RequestDataValidationException;
import com.example.paymentservice.service.IdempotencyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Slf4j
@Order(3)
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdempotencyFilter extends OncePerRequestFilter {

    private static final List<HttpMethod> IDEMPOTENT_HTTP_METHODS = List.of(HttpMethod.PATCH, HttpMethod.POST);
    private static final String IDEMPOTENCY_HEADER = "X-Idempotency-Key";

    IdempotencyService idempotencyService;
    ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain) throws IOException, ServletException {
        var httpMethod = HttpMethod.valueOf(request.getMethod());

        if (!IDEMPOTENT_HTTP_METHODS.contains(httpMethod)) {
            filterChain.doFilter(request, response);
            return;
        }

        var idempotencyHeaderVal = request.getHeader(IDEMPOTENCY_HEADER);
        validateIdempotencyHeader(idempotencyHeaderVal);

        var idempotencyKey = UUID.fromString(idempotencyHeaderVal);

        String requestBody = getRequestBody(request);
        String normalizedBody = toOrderedJsonBody(requestBody);

        var idempotencyStore = idempotencyService.tryGet(idempotencyKey, normalizedBody);

        if (IdempotencyStatus.DONE.equals(idempotencyStore.getStatus())) {
            response.setStatus(idempotencyStore.getResponseStatus());
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            response.getWriter().write(idempotencyStore.getResponseBody());
            response.getWriter().flush();
            return;
        }
        RepeatableRequestWrapper requestWrapper = assignNewBodyToWrapRequest(request, normalizedBody);
        var responseWrapper = new ContentCachingResponseWrapper(response);
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);

            byte[] content = responseWrapper.getContentAsByteArray();
            var responseBody = new String(content, StandardCharsets.UTF_8);
            int responseStatus = responseWrapper.getStatus();

            idempotencyService.markAsDone(idempotencyKey, responseBody, responseStatus);
        } finally {
            responseWrapper.copyBodyToResponse();
        }
    }

    private RepeatableRequestWrapper assignNewBodyToWrapRequest(HttpServletRequest request, String reqBody) throws UnsupportedEncodingException {
        byte[] newReqBody = reqBody.getBytes(StandardCharsets.UTF_8);
        var requestWrapper = new RepeatableRequestWrapper(request, newReqBody);
        requestWrapper.setContentType(MediaType.APPLICATION_JSON_VALUE);
        return requestWrapper;
    }

    private String getRequestBody(HttpServletRequest request) throws IOException {
        byte[] content = request.getInputStream().readAllBytes();
        return new String(content, StandardCharsets.UTF_8);
    }

    private String toOrderedJsonBody(String requestBody) {
        try {
            var obj = objectMapper.readValue(requestBody, Object.class);

            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("При отсортировки json выявилась ошибка: {}", e.getMessage(), e);
            var errorMessage = "Для данных endpoint'ов необходимо передать json content-type";
            throw new RequestDataValidationException(errorMessage);
        }
    }

    private void validateIdempotencyHeader(String idempotencyHeaderVal) {
        if (!StringUtils.hasText(idempotencyHeaderVal)) {
            log.error("Сторона интеграции не передает заголовок idempotency ({})", IDEMPOTENCY_HEADER);
            var errorMessage = String.format("Заголовок idempotency (%s) пуст.", IDEMPOTENCY_HEADER);
            throw new RequestDataValidationException(errorMessage);
        }
        if (!isValidUUID(idempotencyHeaderVal)) {
            log.error("Сторона интеграции некорректно передает значение заголовка idempotency ({})", IDEMPOTENCY_HEADER);
            var errorMessage = String.format("Некорректное значение заголовка idempotency (%s).", IDEMPOTENCY_HEADER);
            throw new RequestDataValidationException(errorMessage);
        }
    }

    private boolean isValidUUID(String value) {
        try {
            UUID.fromString(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
