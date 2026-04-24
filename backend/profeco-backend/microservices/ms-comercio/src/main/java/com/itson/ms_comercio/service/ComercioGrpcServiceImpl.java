package com.itson.ms_comercio.service;


import com.mycompany.grpc.comercio.ComercioServiceGrpc;
import com.mycompany.grpc.comercio.Empty;
import com.mycompany.grpc.comercio.PingResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService // Anotación clave: Levanta este servicio en el puerto 9092
public class ComercioGrpcServiceImpl extends ComercioServiceGrpc.ComercioServiceImplBase {

    @Override
    public void ping(Empty request, StreamObserver<PingResponse> responseObserver) {
        // 1. Construimos la respuesta generada por tu .proto
        PingResponse response = PingResponse.newBuilder()
                .setMensaje("Pong! Conectado a ms-comercio exitosamente.")
                .build();

        // 2. Enviamos la respuesta por la red
        responseObserver.onNext(response);
        // 3. Cerramos la conexión
        responseObserver.onCompleted();
    }
}