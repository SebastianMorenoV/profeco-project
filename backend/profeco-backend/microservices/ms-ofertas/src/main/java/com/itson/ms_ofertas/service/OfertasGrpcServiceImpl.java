package com.itson.ms_ofertas.service;

import com.itson.ms_ofertas.entity.Oferta;
import com.itson.ms_ofertas.repository.OfertaRepository;
import com.itson.ms_ofertas.config.RabbitMQConfig;
import com.itson.ms_ofertas.dto.OfertaEventDTO;

import com.mycompany.grpc.ofertas.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@GrpcService
public class OfertasGrpcServiceImpl extends OfertasServiceGrpc.OfertasServiceImplBase {

    @Autowired
    private OfertaRepository ofertaRepo;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void ping(Empty request, StreamObserver<PingResponse> responseObserver) {
        responseObserver.onNext(PingResponse.newBuilder()
                .setMensaje("Pong! ms-ofertas conectado y funcionando.").build());
        responseObserver.onCompleted();
    }

    @Override
    public void publicarOferta(PublicarOfertaRequest request, StreamObserver<OfertaResponse> responseObserver) {
        Oferta entity = new Oferta();
        entity.setComercioId(request.getComercioId());
        entity.setTitulo(request.getTitulo());
        entity.setDescripcion(request.getDescripcion());
        entity.setPrecioOriginal(BigDecimal.valueOf(request.getPrecioOriginal()));
        entity.setPrecioOferta(BigDecimal.valueOf(request.getPrecioOferta()));
        entity.setPorcentajeDescuento(BigDecimal.valueOf(request.getPorcentajeDescuento()));
        entity.setFechaInicio(LocalDate.parse(request.getFechaInicio()));
        entity.setFechaFin(LocalDate.parse(request.getFechaFin()));

        Oferta saved = ofertaRepo.save(entity);

        // Enviar evento a RabbitMQ
        try {
            OfertaEventDTO evento = new OfertaEventDTO(
                    saved.getTitulo(),
                    saved.getDescripcion(),
                    saved.getPrecioOriginal().doubleValue(),
                    saved.getPrecioOferta().doubleValue(),
                    saved.getPorcentajeDescuento().doubleValue()
            );
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, evento);
        } catch (Exception e) {
            System.err.println("⚠️ No se pudo enviar evento a RabbitMQ: " + e.getMessage());
        }

        responseObserver.onNext(OfertaResponse.newBuilder().setOferta(toProto(saved)).build());
        responseObserver.onCompleted();
    }

    @Override
    public void obtenerOferta(IdRequest request, StreamObserver<OfertaResponse> responseObserver) {
        ofertaRepo.findById(request.getId()).ifPresentOrElse(
                o -> {
                    responseObserver.onNext(OfertaResponse.newBuilder().setOferta(toProto(o)).build());
                    responseObserver.onCompleted();
                },
                () -> responseObserver.onError(io.grpc.Status.NOT_FOUND
                        .withDescription("Oferta no encontrada con ID: " + request.getId()).asRuntimeException())
        );
    }

    @Override
    public void listarOfertas(ListarOfertasRequest request, StreamObserver<ListaOfertasResponse> responseObserver) {
        List<Oferta> resultados = request.getSoloActivas()
                ? ofertaRepo.findByActivaTrue()
                : ofertaRepo.findAll();

        ListaOfertasResponse.Builder builder = ListaOfertasResponse.newBuilder();
        resultados.forEach(o -> builder.addOfertas(toProto(o)));
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void actualizarOferta(ActualizarOfertaRequest request, StreamObserver<OfertaResponse> responseObserver) {
        ofertaRepo.findById(request.getId()).ifPresentOrElse(
                o -> {
                    if (!request.getTitulo().isEmpty()) o.setTitulo(request.getTitulo());
                    if (!request.getDescripcion().isEmpty()) o.setDescripcion(request.getDescripcion());
                    if (request.getPrecioOriginal() > 0) o.setPrecioOriginal(BigDecimal.valueOf(request.getPrecioOriginal()));
                    if (request.getPrecioOferta() > 0) o.setPrecioOferta(BigDecimal.valueOf(request.getPrecioOferta()));
                    if (request.getPorcentajeDescuento() > 0) o.setPorcentajeDescuento(BigDecimal.valueOf(request.getPorcentajeDescuento()));
                    if (!request.getFechaInicio().isEmpty()) o.setFechaInicio(LocalDate.parse(request.getFechaInicio()));
                    if (!request.getFechaFin().isEmpty()) o.setFechaFin(LocalDate.parse(request.getFechaFin()));

                    Oferta saved = ofertaRepo.save(o);
                    responseObserver.onNext(OfertaResponse.newBuilder().setOferta(toProto(saved)).build());
                    responseObserver.onCompleted();
                },
                () -> responseObserver.onError(io.grpc.Status.NOT_FOUND
                        .withDescription("Oferta no encontrada con ID: " + request.getId()).asRuntimeException())
        );
    }

    @Override
    public void eliminarOferta(IdRequest request, StreamObserver<MensajeResponse> responseObserver) {
        ofertaRepo.findById(request.getId()).ifPresentOrElse(
                o -> {
                    o.setActiva(false);
                    ofertaRepo.save(o);
                    responseObserver.onNext(MensajeResponse.newBuilder()
                            .setMensaje("Oferta desactivada correctamente.").setExito(true).build());
                    responseObserver.onCompleted();
                },
                () -> responseObserver.onError(io.grpc.Status.NOT_FOUND
                        .withDescription("Oferta no encontrada con ID: " + request.getId()).asRuntimeException())
        );
    }

    @Override
    public void listarOfertasPorComercio(IdRequest request, StreamObserver<ListaOfertasResponse> responseObserver) {
        List<Oferta> resultados = ofertaRepo.findByComercioIdAndActivaTrue(request.getId());
        ListaOfertasResponse.Builder builder = ListaOfertasResponse.newBuilder();
        resultados.forEach(o -> builder.addOfertas(toProto(o)));
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    private com.mycompany.grpc.ofertas.Oferta toProto(Oferta e) {
        return com.mycompany.grpc.ofertas.Oferta.newBuilder()
                .setId(e.getId())
                .setComercioId(e.getComercioId())
                .setTitulo(e.getTitulo())
                .setDescripcion(e.getDescripcion() != null ? e.getDescripcion() : "")
                .setPrecioOriginal(e.getPrecioOriginal().doubleValue())
                .setPrecioOferta(e.getPrecioOferta().doubleValue())
                .setPorcentajeDescuento(e.getPorcentajeDescuento() != null ? e.getPorcentajeDescuento().doubleValue() : 0)
                .setFechaInicio(e.getFechaInicio().toString())
                .setFechaFin(e.getFechaFin().toString())
                .setActiva(e.getActiva())
                .setFechaCreacion(e.getFechaCreacion().toString())
                .build();
    }
}
