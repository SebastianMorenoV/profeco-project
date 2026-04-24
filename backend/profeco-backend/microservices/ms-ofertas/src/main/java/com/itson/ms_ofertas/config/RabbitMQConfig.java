package com.itson.ms_ofertas.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE = "ofertas.nuevas.queue";
    public static final String EXCHANGE = "ofertas.exchange";
    public static final String ROUTING_KEY = "ofertas.routingkey";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE, true); // true = sobrevive reinicios
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    // Y en tu método usa la nueva clase:
    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
