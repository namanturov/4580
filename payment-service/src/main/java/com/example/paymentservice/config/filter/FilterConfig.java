package com.example.paymentservice.config.filter;

import com.example.paymentservice.filter.FilterChainExceptionHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
public class FilterConfig {

    @Bean
    public FilterChainExceptionHandler filterChainExceptionHandler(@Qualifier("handlerExceptionResolver")
                                                                   HandlerExceptionResolver handlerExceptionResolver) {
        return new FilterChainExceptionHandler(handlerExceptionResolver);
    }
}
