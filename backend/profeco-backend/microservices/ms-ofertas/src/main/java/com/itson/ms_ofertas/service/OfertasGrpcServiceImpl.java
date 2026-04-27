package com.itson.ms_ofertas.service;

import com.itson.ms_ofertas.config.RabbitMQConfig;
import com.itson.ms_ofertas.dto.OfertaEventDTO;
import com.mycompany.grpc.ofertas.OfertasServiceGrpc;
import com.mycompany.grpc.ofertas.Empty;
import com.mycompany.grpc.ofertas.PingResponse;
import com.mycompany.grpc.ofertas.PublicarOfertaRequest;
import com.mycompany.grpc.ofertas.PublicarOfertaResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class OfertasGrpcServiceImpl extends OfertasServiceGrpc.OfertasServiceImplBase {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void ping(Empty request, StreamObserver<PingResponse> responseObserver) {
        PingResponse response = PingResponse.newBuilder()
                .setMensaje("Pong! Conectado a ms-ofertas exitosamente.")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void publicarOferta(PublicarOfertaRequest request, StreamObserver<PublicarOfertaResponse> responseObserver) {

        OfertaEventDTO oferta = new OfertaEventDTO(
                request.getTitulo(),
                request.getDescripcion(),
                request.getPrecioOriginal(),
                request.getPrecioOferta(),
                request.getPorcentajeDescuento()
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                oferta
        );

        String mensajeExito = "Mensaje enviado a RabbitMQ exitosamente: " + oferta.getTitulo();

        PublicarOfertaResponse response = PublicarOfertaResponse.newBuilder()
                .setMensaje(mensajeExito)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
