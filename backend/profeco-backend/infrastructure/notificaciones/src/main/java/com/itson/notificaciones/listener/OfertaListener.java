package com.itson.notificaciones.listener;


import com.itson.notificaciones.dto.OfertaEventDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OfertaListener {

    @RabbitListener(queues = "ofertas.nuevas.queue")
    public void procesarNuevaOferta(OfertaEventDTO oferta) {
        System.out.println("\n==========================================");
        System.out.println("📲 [Notificaciones] ALERTA DE NUEVA OFERTA");
        System.out.println("Producto: " + oferta.getTitulo());
        System.out.println("Detalle: " + oferta.getDescripcion());
        System.out.println("Precio Anterior: $" + oferta.getPrecioOriginal());
        System.out.println("🔥 Precio OFERTA: $" + oferta.getPrecioOferta());
        System.out.println("📉 Descuento Aplicado: " + oferta.getPorcentajeDescuento() + "%");
        System.out.println("==========================================\n");
    }
}