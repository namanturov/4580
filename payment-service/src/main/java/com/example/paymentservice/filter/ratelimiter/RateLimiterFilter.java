package com.example.paymentservice.filter.ratelimiter;

import com.example.paymentservice.exception.RateLimitExceededException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Order(2)
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RateLimiterFilter extends OncePerRequestFilter {

    @Value("${rate.limiter.buckets.capacity}")
    int capacity;
    @Value("${rate.limiter.refill.tokens}")
    int refillTokens;
    @Value("${rate.limiter.refill.period}")
    Duration refillPeriod;

    private static final ConcurrentHashMap<String, Bucket> BUCKETS = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain) throws IOException, ServletException {
        var remoteAddr = request.getRemoteAddr();
        var bucket = BUCKETS.computeIfAbsent(remoteAddr, this::createBucket);

        log.debug("Доступно " + bucket.getAvailableTokens() + " токенов");

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            log.error("Данный клиент ({}) исчерпал все токены", remoteAddr);
            throw new RateLimitExceededException("Вы исчерпали все токены, мда.");
        }
    }

    private Bucket createBucket(String ipAddr) {
        Bandwidth limit = Bandwidth.builder()
                .capacity(capacity)
                .refillIntervally(refillTokens, refillPeriod)
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
