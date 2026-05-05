package com.itson.notificaciones.listener;

import com.itson.notificaciones.dto.MultaEventDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MultaListener {

    @Autowired
    private JavaMailSender mailSender; // Se inyecta el servicio de correos de Spring

    @RabbitListener(queues = "multas.nuevas.queue")
    public void procesarNuevaMulta(MultaEventDTO multa) {
        System.out.println("\n==========================================");
        System.out.println("⚠️ [Notificaciones] ALERTA DE MULTA DE PROFECO ⚠️");
        System.out.println("Comercio Sancionado ID: " + multa.getComercioId());
        System.out.println("Motivo: " + multa.getMotivo());
        System.out.println("Monto a pagar: $" + multa.getMonto());
        System.out.println("Detalles: " + multa.getDescripcion());
        
        // Llamamos al método para enviar el correo
        enviarCorreoMulta(multa);

        System.out.println("==========================================\n");
    }

    private void enviarCorreoMulta(MultaEventDTO multa) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            
            // Correos de prueba solicitados
            String[] destinatarios = {
                "erick.moreno252840@potros.itson.edu.mx", 
                "jose.aguilar252049@potros.itson.edu.mx",
                "luciano.barcelo252086@potros.itson.edu.mx",
                "Benjamin.soto253183@potros.itson.edu.mx",
                "jose.islas252574@potros.itson.edu.mx"
            };
            
            mensaje.setTo(destinatarios);
            mensaje.setSubject("⚠️ ALERTA DE MULTA DE PROFECO - Comercio ID: " + multa.getComercioId());
            
            // Cuerpo del correo usando los datos del DTO
            String cuerpoCorreo = "Se ha registrado una nueva multa para su establecimiento.\n\n" +
                                  "Detalles de la infracción:\n" +
                                  "- Motivo: " + multa.getMotivo() + "\n" +
                                  "- Monto a pagar: $" + multa.getMonto() + "\n" +
                                  "- Descripción: " + multa.getDescripcion() + "\n\n" +
                                  "Por favor, atienda esta resolución lo antes posible.";
            
            mensaje.setText(cuerpoCorreo);
            
            mailSender.send(mensaje);
            System.out.println("✅ Correo de multa enviado exitosamente a los establecimientos de prueba.");
            
        } catch (Exception e) {
            System.err.println("❌ Error al enviar el correo de multa: " + e.getMessage());
        }
    }
}