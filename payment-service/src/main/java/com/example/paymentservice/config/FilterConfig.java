package com.example.paymentservice.config;

import com.example.paymentservice.filter.exceptionhandler.ExceptionHandlerFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
public class FilterConfig {

    @Bean
    public ExceptionHandlerFilter filterChainExceptionHandler(@Qualifier("handlerExceptionResolver")
                                                                   HandlerExceptionResolver handlerExceptionResolver) {
        return new ExceptionHandlerFilter(handlerExceptionResolver);
    }
}
