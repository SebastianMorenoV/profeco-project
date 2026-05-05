package com.itson.ms_multas.config;

import org.springframework.amqp.core.*;
// Usa Jackson2JsonMessageConverter en lugar de JacksonJsonMessageConverter[cite: 6]
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String QUEUE = "multas.nuevas.queue";
    public static final String EXCHANGE = "multas.exchange";
    public static final String ROUTING_KEY = "multas.routingkey";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE, true); 
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        // Asegúrate de que retorne la versión 2[cite: 6]
        return new Jackson2JsonMessageConverter();
    }
}