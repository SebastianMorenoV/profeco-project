package com.itson.ms_resenias.services;

import com.itson.ms_resenias.entity.Resenia;
import com.itson.ms_resenias.repository.ReseniaRepository;

import com.mycompany.grpc.resenias.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@GrpcService
public class ReseniasGrpcServiceImpl extends ReseniasServiceGrpc.ReseniasServiceImplBase {

    @Autowired
    private ReseniaRepository reseniaRepo;

    @Override
    public void ping(Empty request, StreamObserver<PingResponse> responseObserver) {
        responseObserver.onNext(PingResponse.newBuilder()
                .setMensaje("Pong! ms-resenias conectado y funcionando.").build());
        responseObserver.onCompleted();
    }

    @Override
    public void crearResenia(CrearReseniaRequest request, StreamObserver<ReseniaResponse> responseObserver) {
        Resenia entity = new Resenia();
        entity.setUsuarioId(request.getUsuarioId());
        entity.setComercioId(request.getComercioId());
        entity.setCalificacion(request.getCalificacion());
        entity.setComentario(request.getComentario());

        Resenia saved = reseniaRepo.save(entity);
        responseObserver.onNext(ReseniaResponse.newBuilder().setResenia(toProto(saved)).build());
        responseObserver.onCompleted();
    }

    @Override
    public void obtenerResenia(IdRequest request, StreamObserver<ReseniaResponse> responseObserver) {
        reseniaRepo.findById(request.getId()).ifPresentOrElse(
                r -> {
                    responseObserver.onNext(ReseniaResponse.newBuilder().setResenia(toProto(r)).build());
                    responseObserver.onCompleted();
                },
                () -> responseObserver.onError(io.grpc.Status.NOT_FOUND
                        .withDescription("Reseña no encontrada con ID: " + request.getId()).asRuntimeException())
        );
    }

    @Override
    public void listarPorComercio(IdRequest request, StreamObserver<ListaReseniasResponse> responseObserver) {
        List<Resenia> resultados = reseniaRepo.findByComercioId(request.getId());
        ListaReseniasResponse.Builder builder = ListaReseniasResponse.newBuilder();
        resultados.forEach(r -> builder.addResenias(toProto(r)));
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void actualizarResenia(ActualizarReseniaRequest request, StreamObserver<ReseniaResponse> responseObserver) {
        reseniaRepo.findById(request.getId()).ifPresentOrElse(
                r -> {
                    if (request.getCalificacion() > 0) r.setCalificacion(request.getCalificacion());
                    if (!request.getComentario().isEmpty()) r.setComentario(request.getComentario());
                    Resenia saved = reseniaRepo.save(r);
                    responseObserver.onNext(ReseniaResponse.newBuilder().setResenia(toProto(saved)).build());
                    responseObserver.onCompleted();
                },
                () -> responseObserver.onError(io.grpc.Status.NOT_FOUND
                        .withDescription("Reseña no encontrada con ID: " + request.getId()).asRuntimeException())
        );
    }

    @Override
    public void eliminarResenia(IdRequest request, StreamObserver<MensajeResponse> responseObserver) {
        if (reseniaRepo.existsById(request.getId())) {
            reseniaRepo.deleteById(request.getId());
            responseObserver.onNext(MensajeResponse.newBuilder()
                    .setMensaje("Reseña eliminada correctamente.").setExito(true).build());
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(io.grpc.Status.NOT_FOUND
                    .withDescription("Reseña no encontrada con ID: " + request.getId()).asRuntimeException());
        }
    }

    @Override
    public void obtenerPromedio(IdRequest request, StreamObserver<PromedioResponse> responseObserver) {
        Double promedio = reseniaRepo.findPromedioByComercioId(request.getId());
        Long total = reseniaRepo.countByComercioId(request.getId());

        responseObserver.onNext(PromedioResponse.newBuilder()
                .setComercioId(request.getId())
                .setPromedio(promedio != null ? promedio : 0.0)
                .setTotalResenias(total != null ? total.intValue() : 0)
                .build());
        responseObserver.onCompleted();
    }

    private com.mycompany.grpc.resenias.Resenia toProto(Resenia e) {
        return com.mycompany.grpc.resenias.Resenia.newBuilder()
                .setId(e.getId())
                .setUsuarioId(e.getUsuarioId())
                .setComercioId(e.getComercioId())
                .setCalificacion(e.getCalificacion())
                .setComentario(e.getComentario() != null ? e.getComentario() : "")
                .setFechaCreacion(e.getFechaCreacion().toString())
                .build();
    }
}