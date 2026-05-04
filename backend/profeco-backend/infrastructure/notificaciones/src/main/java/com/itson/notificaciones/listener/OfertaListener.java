package com.itson.notificaciones.listener;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.itson.notificaciones.dto.OfertaEventDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class OfertaListener {

    private static final String TOPIC_OFERTAS = "ofertas-nuevas";

    @Autowired
    private FirebaseMessaging firebaseMessaging;

    @RabbitListener(queues = "ofertas.nuevas.queue")
    public void procesarNuevaOferta(OfertaEventDTO oferta) {
        System.out.println("\n==========================================");
        System.out.println("📲 [Notificaciones] Nueva oferta recibida");
        System.out.println("Producto: " + oferta.getTitulo());
        System.out.println("Detalle: " + oferta.getDescripcion());
        System.out.println("Precio Anterior: $" + oferta.getPrecioOriginal());
        System.out.println("🔥 Precio OFERTA: $" + oferta.getPrecioOferta());
        System.out.println("📉 Descuento: " + oferta.getPorcentajeDescuento() + "%");

        enviarPushAlTopic(oferta);

        System.out.println("==========================================\n");
    }

    private void enviarPushAlTopic(OfertaEventDTO oferta) {
        String titulo = "Nueva oferta: " + oferta.getTitulo();
        String cuerpo = String.format(
                "Antes $%.2f → Ahora $%.2f (%.0f%% off)",
                oferta.getPrecioOriginal(),
                oferta.getPrecioOferta(),
                oferta.getPorcentajeDescuento()
        );

        Map<String, String> data = new HashMap<>();
        data.put("titulo", titulo);
        data.put("cuerpo", cuerpo);
        data.put("descripcion", oferta.getDescripcion() == null ? "" : oferta.getDescripcion());
        data.put("precioOriginal", String.valueOf(oferta.getPrecioOriginal()));
        data.put("precioOferta", String.valueOf(oferta.getPrecioOferta()));
        data.put("porcentajeDescuento", String.valueOf(oferta.getPorcentajeDescuento()));

        Message mensaje = Message.builder()
                .setTopic(TOPIC_OFERTAS)
                .setNotification(
                        Notification.builder()
                                .setTitle(titulo)
                                .setBody(cuerpo)
                                .build()
                )
                .putAllData(data)
                .build();

        try {
            String responseId = firebaseMessaging.send(mensaje);
            System.out.println("✅ Push enviado a FCM (topic " + TOPIC_OFERTAS + "). Mensaje: " + responseId);
        } catch (FirebaseMessagingException e) {
            System.err.println("❌ Error enviando push FCM: " + e.getMessagingErrorCode() + " — " + e.getMessage());
        }
    }
}
