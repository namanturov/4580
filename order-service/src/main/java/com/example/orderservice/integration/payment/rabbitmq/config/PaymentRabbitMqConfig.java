package com.example.orderservice.integration.payment.rabbitmq.config;

import com.example.orderservice.integration.payment.rabbitmq.config.properties.PaymentRabbitMqProperties;
import com.example.orderservice.integration.payment.rabbitmq.dto.request.PaymentRequestMessage;
import com.example.orderservice.integration.payment.rabbitmq.dto.response.PaymentResponseMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(PaymentRabbitMqProperties.class)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentRabbitMqConfig {

    PaymentRabbitMqProperties props;

    @Bean
    public Queue paymentCreateQueue() {
        return QueueBuilder.durable(props.createPayment().queue())
                .build();
    }

    @Bean
    public DirectExchange paymentCreateExchange() {
        return new DirectExchange(props.exchange());
    }

    @Bean
    public Binding queueBinding() {
        return BindingBuilder
                .bind(paymentCreateQueue())
                .to(paymentCreateExchange())
                .with(props.createPayment().routingKey());
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        var rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMapper());
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory){
        var factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMapper());
        factory.setDefaultRequeueRejected(false);
        factory.setAutoStartup(true);
        return factory;
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMapper(){
        var converter = new Jackson2JsonMessageConverter();
        converter.setClassMapper(classMapper());
        converter.getJavaTypeMapper().addTrustedPackages("com.example.orderservice", "java");
        return converter;
    }

    @Bean
    public DefaultClassMapper classMapper(){
        DefaultClassMapper classMapper = new DefaultClassMapper();
        Map<String, Class<?>> idClassMapping = new HashMap<>();

        idClassMapping.put("payment-request", PaymentRequestMessage.class);
        idClassMapping.put("payment-response", PaymentResponseMessage.class);

        classMapper.setIdClassMapping(idClassMapping);
        return classMapper;
    }
}
