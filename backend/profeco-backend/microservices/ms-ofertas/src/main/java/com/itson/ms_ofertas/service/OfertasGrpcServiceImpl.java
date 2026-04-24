package com.itson.ms_ofertas.service;


import com.mycompany.grpc.ofertas.OfertasServiceGrpc;
import com.mycompany.grpc.ofertas.Empty;
import com.mycompany.grpc.ofertas.PingResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService // Anotación clave: Levanta este servicio en el puerto 9092
public class OfertasGrpcServiceImpl extends OfertasServiceGrpc.OfertasServiceImplBase {

    @Override
    public void ping(Empty request, StreamObserver<PingResponse> responseObserver) {
        // 1. Construimos la respuesta generada por tu .proto
        PingResponse response = PingResponse.newBuilder()
                .setMensaje("Pong! Conectado a ms-ofertas exitosamente.")
                .build();

        // 2. Enviamos la respuesta por la red
        responseObserver.onNext(response);
        // 3. Cerramos la conexión
        responseObserver.onCompleted();
    }
}