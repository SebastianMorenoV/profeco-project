package com.itson.notificaciones.listener;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.itson.notificaciones.dto.OfertaEventDTO;
import com.mycompany.grpc.usuarios.IdRequest;
import com.mycompany.grpc.usuarios.ListaTokensResponse;
import com.mycompany.grpc.usuarios.UsuariosServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OfertaListener {

    @Value("${profeco.fcm.fallback-topic:ofertas-nuevas}")
    private String fallbackTopic;

    @Autowired
    private FirebaseMessaging firebaseMessaging;

    @GrpcClient("ms-usuarios")
    private UsuariosServiceGrpc.UsuariosServiceBlockingStub usuariosStub;

    @RabbitListener(queues = "ofertas.nuevas.queue")
    public void procesarNuevaOferta(OfertaEventDTO oferta) {
        System.out.println("\n==========================================");
        System.out.println("📲 [Notificaciones] Nueva oferta recibida");
        System.out.println("Comercio: " + oferta.getComercioId());
        System.out.println("Producto: " + oferta.getTitulo());
        System.out.println("Detalle: " + oferta.getDescripcion());
        System.out.println("Precio Anterior: $" + oferta.getPrecioOriginal());
        System.out.println("🔥 Precio OFERTA: $" + oferta.getPrecioOferta());
        System.out.println("📉 Descuento: " + oferta.getPorcentajeDescuento() + "%");
        System.out.println("🏷️ Tipo Promoción: " + (oferta.getTipoPromocion() != null ? oferta.getTipoPromocion() : "N/A"));
        System.out.println("📦 Producto ID: " + (oferta.getProductoId() != null ? oferta.getProductoId() : "Sin asociar"));

        List<String> tokens = resolverTokens(oferta.getComercioId());
        if (tokens.isEmpty()) {
            System.out.println("ℹ️ Sin tokens registrados para favoritos del comercio "
                    + oferta.getComercioId() + " — fallback al topic " + fallbackTopic);
            enviarPushAlTopic(oferta);
        } else {
            System.out.println("📨 Enviando push a " + tokens.size() + " dispositivo(s) suscritos al comercio "
                    + oferta.getComercioId());
            enviarPushPorTokens(oferta, tokens);
        }

        System.out.println("==========================================\n");
    }

    private List<String> resolverTokens(Long comercioId) {
        if (comercioId == null) return List.of();
        try {
            ListaTokensResponse resp = usuariosStub.listarFcmTokensPorComercio(
                    IdRequest.newBuilder().setId(comercioId).build()
            );
            return resp.getTokensList();
        } catch (Exception e) {
            System.err.println("⚠️ No se pudieron resolver tokens vía gRPC: " + e.getMessage());
            return List.of();
        }
    }

    private void enviarPushAlTopic(OfertaEventDTO oferta) {
        if (firebaseMessaging == null) {
            System.err.println("⚠️ Push FCM al topic omitido (FirebaseMessaging no disponible).");
            return;
        }

        Datos d = construirDatos(oferta);

        Message mensaje = Message.builder()
                .setTopic(fallbackTopic)
                .setNotification(Notification.builder().setTitle(d.titulo).setBody(d.cuerpo).build())
                .putAllData(d.data)
                .build();

        try {
            String responseId = firebaseMessaging.send(mensaje);
            System.out.println("✅ Push enviado a FCM (topic " + fallbackTopic + "). Mensaje: " + responseId);
        } catch (FirebaseMessagingException e) {
            System.err.println("❌ Error enviando push FCM: " + e.getMessagingErrorCode() + " — " + e.getMessage());
        }
    }

    private void enviarPushPorTokens(OfertaEventDTO oferta, List<String> tokens) {
        if (firebaseMessaging == null) {
            System.err.println("⚠️ Push FCM por tokens omitido (FirebaseMessaging no disponible).");
            return;
        }

        Datos d = construirDatos(oferta);

        // FCM acepta hasta 500 tokens por sendEachForMulticast.
        int batchSize = 500;
        for (int i = 0; i < tokens.size(); i += batchSize) {
            List<String> chunk = tokens.subList(i, Math.min(i + batchSize, tokens.size()));
            MulticastMessage mensaje = MulticastMessage.builder()
                    .addAllTokens(chunk)
                    .setNotification(Notification.builder().setTitle(d.titulo).setBody(d.cuerpo).build())
                    .putAllData(d.data)
                    .build();
            try {
                var resp = firebaseMessaging.sendEachForMulticast(mensaje);
                System.out.println("✅ Multicast: " + resp.getSuccessCount() + " ok / "
                        + resp.getFailureCount() + " fallidos");
            } catch (FirebaseMessagingException e) {
                System.err.println("❌ Error en multicast FCM: " + e.getMessagingErrorCode() + " — " + e.getMessage());
            }
        }
    }

    private Datos construirDatos(OfertaEventDTO oferta) {
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
        data.put("comercioId", String.valueOf(oferta.getComercioId()));
        data.put("precioOriginal", String.valueOf(oferta.getPrecioOriginal()));
        data.put("precioOferta", String.valueOf(oferta.getPrecioOferta()));
        data.put("porcentajeDescuento", String.valueOf(oferta.getPorcentajeDescuento()));
        data.put("tipoPromocion", oferta.getTipoPromocion() != null ? oferta.getTipoPromocion() : "");
        data.put("productoId", oferta.getProductoId() != null ? String.valueOf(oferta.getProductoId()) : "0");
        return new Datos(titulo, cuerpo, data);
    }

    private record Datos(String titulo, String cuerpo, Map<String, String> data) {}
}
