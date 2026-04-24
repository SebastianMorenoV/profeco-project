package com.itson.ms_ofertas.controller;


import com.itson.ms_ofertas.config.RabbitMQConfig;
import com.itson.ms_ofertas.dto.OfertaEventDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ofertas")
public class OfertaRESTController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping("/publicar")
    public String publicarOferta(@RequestBody OfertaEventDTO oferta) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE, 
                RabbitMQConfig.ROUTING_KEY, 
                oferta
        );
        return "Mensaje enviado a RabbitMQ exitosamente: " + oferta.getTitulo();
    }
}