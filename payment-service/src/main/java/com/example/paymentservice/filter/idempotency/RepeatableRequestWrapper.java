package com.example.paymentservice.filter.idempotency;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RepeatableRequestWrapper extends HttpServletRequestWrapper {
    byte[] cachedBody;
    @NonFinal
    String overrideContentType;

    public RepeatableRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        cachedBody = request.getInputStream().readAllBytes();
    }

    public RepeatableRequestWrapper(HttpServletRequest request, byte[] body) {
        super(request);
        cachedBody = body;
    }

    @Override
    public ServletInputStream getInputStream() {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(cachedBody);
        return new ServletInputStream() {
            @Override
            public int read() {
                return byteArrayInputStream.read();
            }

            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener listener) {
                // ignored
            }
        };
    }

    public void setContentType(String contentType) {
        this.overrideContentType = contentType;
    }

    @Override
    public String getContentType() {
        return overrideContentType != null
                ? overrideContentType
                : super.getContentType();
    }

    @Override
    public String getHeader(String name) {
        if ("Content-Type".equalsIgnoreCase(name) && overrideContentType != null) {
            return overrideContentType;
        } else return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        if ("Content-Type".equalsIgnoreCase(name) && overrideContentType != null) {
            return Collections.enumeration(List.of(overrideContentType));
        }
        return super.getHeaders(name);
    }

    public String getCachedBodyAsString() {
        return new String(cachedBody, StandardCharsets.UTF_8);
    }
}
