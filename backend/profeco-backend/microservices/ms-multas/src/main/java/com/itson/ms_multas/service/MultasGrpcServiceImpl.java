package com.itson.ms_multas.service;

import com.itson.ms_multas.entity.Multa;
import com.itson.ms_multas.entity.Reporte;
import com.itson.ms_multas.repository.MultaRepository;
import com.itson.ms_multas.repository.ReporteRepository;

import com.mycompany.grpc.multas.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@GrpcService
public class MultasGrpcServiceImpl extends MultasServiceGrpc.MultasServiceImplBase {

    @Autowired
    private MultaRepository multaRepo;

    @Autowired
    private ReporteRepository reporteRepo;

    // ==================== PING ====================
    @Override
    public void ping(Empty request, StreamObserver<PingResponse> responseObserver) {
        responseObserver.onNext(PingResponse.newBuilder()
                .setMensaje("Pong! ms-multas conectado y funcionando.").build());
        responseObserver.onCompleted();
    }

    // ==================== REPORTES (Consumidores) ====================

    @Override
    public void crearReporte(CrearReporteRequest request, StreamObserver<ReporteResponse> responseObserver) {
        Reporte entity = new Reporte();
        entity.setUsuarioId(request.getUsuarioId());
        entity.setComercioId(request.getComercioId());
        entity.setMotivo(request.getMotivo());
        entity.setDescripcion(request.getDescripcion());

        Reporte saved = reporteRepo.save(entity);
        responseObserver.onNext(ReporteResponse.newBuilder().setReporte(toProtoReporte(saved)).build());
        responseObserver.onCompleted();
    }

    @Override
    public void obtenerReporte(IdRequest request, StreamObserver<ReporteResponse> responseObserver) {
        reporteRepo.findById(request.getId()).ifPresentOrElse(
                r -> {
                    responseObserver.onNext(ReporteResponse.newBuilder().setReporte(toProtoReporte(r)).build());
                    responseObserver.onCompleted();
                },
                () -> responseObserver.onError(io.grpc.Status.NOT_FOUND
                        .withDescription("Reporte no encontrado con ID: " + request.getId()).asRuntimeException())
        );
    }

    @Override
    public void listarReportesPorUsuario(IdRequest request, StreamObserver<ListaReportesResponse> responseObserver) {
        List<Reporte> resultados = reporteRepo.findByUsuarioId(request.getId());
        ListaReportesResponse.Builder builder = ListaReportesResponse.newBuilder();
        resultados.forEach(r -> builder.addReportes(toProtoReporte(r)));
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void listarReportes(ListarReportesRequest request, StreamObserver<ListaReportesResponse> responseObserver) {
        String estatus = request.getEstatus() == null ? "" : request.getEstatus().trim();
        List<Reporte> resultados = estatus.isEmpty()
                ? reporteRepo.findAll()
                : reporteRepo.findByEstatus(estatus);

        ListaReportesResponse.Builder builder = ListaReportesResponse.newBuilder();
        resultados.forEach(r -> builder.addReportes(toProtoReporte(r)));
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void actualizarEstatusReporte(ActualizarEstatusReporteRequest request, StreamObserver<ReporteResponse> responseObserver) {
        reporteRepo.findById(request.getId()).ifPresentOrElse(
                r -> {
                    r.setEstatus(request.getEstatus());
                    Reporte saved = reporteRepo.save(r);
                    responseObserver.onNext(ReporteResponse.newBuilder().setReporte(toProtoReporte(saved)).build());
                    responseObserver.onCompleted();
                },
                () -> responseObserver.onError(io.grpc.Status.NOT_FOUND
                        .withDescription("Reporte no encontrado con ID: " + request.getId()).asRuntimeException())
        );
    }

    // ==================== MULTAS (Solo PROFECO) ====================

    @Override
    public void emitirMulta(EmitirMultaRequest request, StreamObserver<MultaResponse> responseObserver) {
        Multa entity = new Multa();
        entity.setComercioId(request.getComercioId());
        entity.setMotivo(request.getMotivo());
        entity.setDescripcion(request.getDescripcion());
        entity.setMonto(BigDecimal.valueOf(request.getMonto()));
        entity.setEmitidoPorUsuarioId(request.getEmitidoPorUsuarioId());

        // Vincular al reporte si se proporcionó
        if (request.getReporteId() > 0) {
            entity.setReporteId(request.getReporteId());
        }

        Multa saved = multaRepo.save(entity);

        // Si viene de un reporte, actualizar el estatus del reporte
        if (request.getReporteId() > 0) {
            reporteRepo.findById(request.getReporteId()).ifPresent(r -> {
                r.setEstatus("RESUELTA_CON_MULTA");
                r.setMultaId(saved.getId());
                reporteRepo.save(r);
            });
        }

        responseObserver.onNext(MultaResponse.newBuilder().setMulta(toProtoMulta(saved)).build());
        responseObserver.onCompleted();
    }

    @Override
    public void obtenerMulta(IdRequest request, StreamObserver<MultaResponse> responseObserver) {
        multaRepo.findById(request.getId()).ifPresentOrElse(
                m -> {
                    responseObserver.onNext(MultaResponse.newBuilder().setMulta(toProtoMulta(m)).build());
                    responseObserver.onCompleted();
                },
                () -> responseObserver.onError(io.grpc.Status.NOT_FOUND
                        .withDescription("Multa no encontrada con ID: " + request.getId()).asRuntimeException())
        );
    }

    @Override
    public void listarMultas(ListarMultasRequest request, StreamObserver<ListaMultasResponse> responseObserver) {
        String estatus = request.getEstatus() == null ? "" : request.getEstatus().trim();
        List<Multa> resultados = estatus.isEmpty()
                ? multaRepo.findAll()
                : multaRepo.findByEstatus(estatus);

        ListaMultasResponse.Builder builder = ListaMultasResponse.newBuilder();
        resultados.forEach(m -> builder.addMultas(toProtoMulta(m)));
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void actualizarEstatus(ActualizarEstatusRequest request, StreamObserver<MultaResponse> responseObserver) {
        multaRepo.findById(request.getId()).ifPresentOrElse(
                m -> {
                    m.setEstatus(request.getEstatus());
                    if ("PAGADA".equals(request.getEstatus()) || "CANCELADA".equals(request.getEstatus())) {
                        m.setFechaResolucion(LocalDateTime.now());
                    }
                    Multa saved = multaRepo.save(m);
                    responseObserver.onNext(MultaResponse.newBuilder().setMulta(toProtoMulta(saved)).build());
                    responseObserver.onCompleted();
                },
                () -> responseObserver.onError(io.grpc.Status.NOT_FOUND
                        .withDescription("Multa no encontrada con ID: " + request.getId()).asRuntimeException())
        );
    }

    @Override
    public void listarPorComercio(IdRequest request, StreamObserver<ListaMultasResponse> responseObserver) {
        List<Multa> resultados = multaRepo.findByComercioId(request.getId());
        ListaMultasResponse.Builder builder = ListaMultasResponse.newBuilder();
        resultados.forEach(m -> builder.addMultas(toProtoMulta(m)));
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    // ==================== MAPPERS ====================

    private com.mycompany.grpc.multas.Reporte toProtoReporte(Reporte e) {
        return com.mycompany.grpc.multas.Reporte.newBuilder()
                .setId(e.getId())
                .setUsuarioId(e.getUsuarioId())
                .setComercioId(e.getComercioId())
                .setMotivo(e.getMotivo())
                .setDescripcion(e.getDescripcion())
                .setEstatus(e.getEstatus())
                .setFechaCreacion(e.getFechaCreacion().toString())
                .setMultaId(e.getMultaId() != null ? e.getMultaId() : 0)
                .build();
    }

    private com.mycompany.grpc.multas.Multa toProtoMulta(Multa e) {
        return com.mycompany.grpc.multas.Multa.newBuilder()
                .setId(e.getId())
                .setComercioId(e.getComercioId())
                .setMotivo(e.getMotivo())
                .setDescripcion(e.getDescripcion())
                .setMonto(e.getMonto().doubleValue())
                .setEstatus(e.getEstatus())
                .setFechaEmision(e.getFechaEmision().toString())
                .setFechaResolucion(e.getFechaResolucion() != null ? e.getFechaResolucion().toString() : "")
                .setEmitidoPorUsuarioId(e.getEmitidoPorUsuarioId() != null ? e.getEmitidoPorUsuarioId() : 0)
                .setReporteId(e.getReporteId() != null ? e.getReporteId() : 0)
                .build();
    }
}