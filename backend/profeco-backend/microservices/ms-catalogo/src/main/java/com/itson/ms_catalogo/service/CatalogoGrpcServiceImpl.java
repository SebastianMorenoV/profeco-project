package com.itson.ms_catalogo.service;


import com.mycompany.grpc.catalogo.CatalogoServiceGrpc;
import com.mycompany.grpc.catalogo.Empty;
import com.mycompany.grpc.catalogo.PingResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService // Anotación clave: Levanta este servicio en el puerto 9092
public class CatalogoGrpcServiceImpl extends CatalogoServiceGrpc.CatalogoServiceImplBase {

    @Override
    public void ping(Empty request, StreamObserver<PingResponse> responseObserver) {
        // 1. Construimos la respuesta generada por tu .proto
        PingResponse response = PingResponse.newBuilder()
                .setMensaje("Pong! Conectado a ms-catalogo exitosamente.")
                .build();

        // 2. Enviamos la respuesta por la red
        responseObserver.onNext(response);
        // 3. Cerramos la conexión
        responseObserver.onCompleted();
    }
}