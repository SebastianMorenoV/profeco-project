package com.itson.ms_resenias.services;


import com.mycompany.grpc.resenias.ReseniasServiceGrpc;
import com.mycompany.grpc.resenias.Empty;
import com.mycompany.grpc.resenias.PingResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService // Anotación clave: Levanta este servicio en el puerto 9092
public class ReseniasGrpcServiceImpl extends ReseniasServiceGrpc.ReseniasServiceImplBase {

    @Override
    public void ping(Empty request, StreamObserver<PingResponse> responseObserver) {
        // 1. Construimos la respuesta generada por tu .proto
        PingResponse response = PingResponse.newBuilder()
                .setMensaje("Pong! Conectado a ms-resenias exitosamente.")
                .build();

        // 2. Enviamos la respuesta por la red
        responseObserver.onNext(response);
        // 3. Cerramos la conexión
        responseObserver.onCompleted();
    }
}