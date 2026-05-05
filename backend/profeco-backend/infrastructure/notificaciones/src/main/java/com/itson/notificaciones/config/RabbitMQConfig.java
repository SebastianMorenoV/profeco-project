package com.itson.notificaciones.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper; // <-- Nueva importación

@Configuration
public class RabbitMQConfig {
         
    @Bean
    public Queue ofertasQueue() {
        return new Queue("ofertas.nuevas.queue", true); 
    }

    @Bean
    public Queue multasQueue() {
        return new Queue("multas.nuevas.queue", true); 
    }

    @Bean
    public MessageConverter messageConverter() {
        // Al pasarle un ObjectMapper explícito, evitamos el choque de versiones con Firebase
        ObjectMapper mapper = new ObjectMapper();
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(mapper);
        
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTrustedPackages("*");
        converter.setJavaTypeMapper(typeMapper);
        
        return converter;
    }
}