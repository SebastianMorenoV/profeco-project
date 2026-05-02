package com.itson.ms_comercio.service;

import com.itson.ms_comercio.entity.Comercio;
import com.itson.ms_comercio.repository.ComercioRepository;

import com.mycompany.grpc.comercio.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@GrpcService
public class ComercioGrpcServiceImpl extends ComercioServiceGrpc.ComercioServiceImplBase {

    @Autowired
    private ComercioRepository comercioRepo;

    @Override
    public void ping(Empty request, StreamObserver<PingResponse> responseObserver) {
        responseObserver.onNext(PingResponse.newBuilder()
                .setMensaje("Pong! ms-comercio conectado y funcionando.").build());
        responseObserver.onCompleted();
    }

    @Override
    public void registrarComercio(RegistrarComercioRequest request, StreamObserver<ComercioResponse> responseObserver) {
        Comercio entity = new Comercio();
        entity.setNombreComercial(request.getNombreComercial());
        entity.setRazonSocial(request.getRazonSocial());
        entity.setRfc(request.getRfc());
        entity.setDireccion(request.getDireccion());
        entity.setCiudad(request.getCiudad());
        entity.setEstado(request.getEstado());
        entity.setCodigoPostal(request.getCodigoPostal());
        entity.setTelefono(request.getTelefono());
        entity.setEmail(request.getEmail());
        entity.setTipoComercio(request.getTipoComercio());
        entity.setLatitud(request.getLatitud());
        entity.setLongitud(request.getLongitud());
        entity.setIdPropietario(request.getIdPropietario());

        Comercio saved = comercioRepo.save(entity);
        responseObserver.onNext(ComercioResponse.newBuilder().setComercio(toProto(saved)).build());
        responseObserver.onCompleted();
    }

    @Override
    public void obtenerComercio(IdRequest request, StreamObserver<ComercioResponse> responseObserver) {
        comercioRepo.findById(request.getId()).ifPresentOrElse(
                c -> {
                    responseObserver.onNext(ComercioResponse.newBuilder().setComercio(toProto(c)).build());
                    responseObserver.onCompleted();
                },
                () -> responseObserver.onError(io.grpc.Status.NOT_FOUND
                        .withDescription("Comercio no encontrado con ID: " + request.getId()).asRuntimeException())
        );
    }

    @Override
    public void listarComercios(ListarComerciosRequest request, StreamObserver<ListaComerciosResponse> responseObserver) {
        String ciudad = request.getCiudad() == null ? "" : request.getCiudad().trim();
        String tipo = request.getTipoComercio() == null ? "" : request.getTipoComercio().trim();

        List<Comercio> resultados;
        if (!ciudad.isEmpty() && !tipo.isEmpty()) {
            resultados = comercioRepo.findByCiudadAndTipoComercioAndActivoTrue(ciudad, tipo);
        } else if (!ciudad.isEmpty()) {
            resultados = comercioRepo.findByCiudadAndActivoTrue(ciudad);
        } else if (!tipo.isEmpty()) {
            resultados = comercioRepo.findByTipoComercioAndActivoTrue(tipo);
        } else {
            resultados = comercioRepo.findByActivoTrue();
        }

        ListaComerciosResponse.Builder builder = ListaComerciosResponse.newBuilder();
        resultados.forEach(c -> builder.addComercios(toProto(c)));
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void actualizarComercio(ActualizarComercioRequest request, StreamObserver<ComercioResponse> responseObserver) {
        comercioRepo.findById(request.getId()).ifPresentOrElse(
                c -> {
                    if (!request.getNombreComercial().isEmpty()) c.setNombreComercial(request.getNombreComercial());
                    if (!request.getRazonSocial().isEmpty()) c.setRazonSocial(request.getRazonSocial());
                    if (!request.getRfc().isEmpty()) c.setRfc(request.getRfc());
                    if (!request.getDireccion().isEmpty()) c.setDireccion(request.getDireccion());
                    if (!request.getCiudad().isEmpty()) c.setCiudad(request.getCiudad());
                    if (!request.getEstado().isEmpty()) c.setEstado(request.getEstado());
                    if (!request.getTelefono().isEmpty()) c.setTelefono(request.getTelefono());
                    if (!request.getEmail().isEmpty()) c.setEmail(request.getEmail());
                    if (!request.getTipoComercio().isEmpty()) c.setTipoComercio(request.getTipoComercio());

                    Comercio saved = comercioRepo.save(c);
                    responseObserver.onNext(ComercioResponse.newBuilder().setComercio(toProto(saved)).build());
                    responseObserver.onCompleted();
                },
                () -> responseObserver.onError(io.grpc.Status.NOT_FOUND
                        .withDescription("Comercio no encontrado con ID: " + request.getId()).asRuntimeException())
        );
    }

    @Override
    public void eliminarComercio(IdRequest request, StreamObserver<MensajeResponse> responseObserver) {
        comercioRepo.findById(request.getId()).ifPresentOrElse(
                c -> {
                    c.setActivo(false);
                    comercioRepo.save(c);
                    responseObserver.onNext(MensajeResponse.newBuilder()
                            .setMensaje("Comercio eliminado correctamente.").setExito(true).build());
                    responseObserver.onCompleted();
                },
                () -> responseObserver.onError(io.grpc.Status.NOT_FOUND
                        .withDescription("Comercio no encontrado con ID: " + request.getId()).asRuntimeException())
        );
    }

    @Override
    public void buscarComercios(BuscarComerciosRequest request, StreamObserver<ListaComerciosResponse> responseObserver) {
        String query = request.getQuery() == null ? "" : request.getQuery().trim();
        List<Comercio> resultados = query.isEmpty()
                ? comercioRepo.findByActivoTrue()
                : comercioRepo.findByNombreComercialContainingIgnoreCaseAndActivoTrue(query);

        ListaComerciosResponse.Builder builder = ListaComerciosResponse.newBuilder();
        resultados.forEach(c -> builder.addComercios(toProto(c)));
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    private com.mycompany.grpc.comercio.Comercio toProto(Comercio e) {
        return com.mycompany.grpc.comercio.Comercio.newBuilder()
                .setId(e.getId())
                .setNombreComercial(e.getNombreComercial())
                .setRazonSocial(e.getRazonSocial() != null ? e.getRazonSocial() : "")
                .setRfc(e.getRfc() != null ? e.getRfc() : "")
                .setDireccion(e.getDireccion())
                .setCiudad(e.getCiudad())
                .setEstado(e.getEstado())
                .setCodigoPostal(e.getCodigoPostal() != null ? e.getCodigoPostal() : "")
                .setTelefono(e.getTelefono() != null ? e.getTelefono() : "")
                .setEmail(e.getEmail() != null ? e.getEmail() : "")
                .setTipoComercio(e.getTipoComercio())
                .setLatitud(e.getLatitud() != null ? e.getLatitud() : 0)
                .setLongitud(e.getLongitud() != null ? e.getLongitud() : 0)
                .setIdPropietario(e.getIdPropietario() != null ? e.getIdPropietario() : 0)
                .setActivo(e.getActivo())
                .setFechaRegistro(e.getFechaRegistro().toString())
                .build();
    }
}