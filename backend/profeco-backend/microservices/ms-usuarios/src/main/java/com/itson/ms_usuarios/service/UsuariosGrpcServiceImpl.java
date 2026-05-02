package com.itson.ms_usuarios.service;

import com.itson.ms_usuarios.entity.Usuario;
import com.itson.ms_usuarios.repository.UsuarioRepository;

import com.mycompany.grpc.usuarios.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@GrpcService
public class UsuariosGrpcServiceImpl extends UsuariosServiceGrpc.UsuariosServiceImplBase {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Override
    public void ping(Empty request, StreamObserver<PingResponse> responseObserver) {
        responseObserver.onNext(PingResponse.newBuilder()
                .setMensaje("Pong! ms-usuarios conectado y funcionando.")
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void registrarUsuario(RegistrarUsuarioRequest request, StreamObserver<UsuarioResponse> responseObserver) {
        Usuario entity = new Usuario();
        entity.setNombre(request.getNombre());
        entity.setApellido(request.getApellido());
        entity.setEmail(request.getEmail());
        entity.setTelefono(request.getTelefono());
        entity.setTipoUsuario(request.getTipoUsuario());

        Usuario saved = usuarioRepo.save(entity);
        responseObserver.onNext(UsuarioResponse.newBuilder().setUsuario(toProto(saved)).build());
        responseObserver.onCompleted();
    }

    @Override
    public void obtenerUsuario(IdRequest request, StreamObserver<UsuarioResponse> responseObserver) {
        usuarioRepo.findById(request.getId()).ifPresentOrElse(
                u -> {
                    responseObserver.onNext(UsuarioResponse.newBuilder().setUsuario(toProto(u)).build());
                    responseObserver.onCompleted();
                },
                () -> responseObserver.onError(io.grpc.Status.NOT_FOUND
                        .withDescription("Usuario no encontrado con ID: " + request.getId())
                        .asRuntimeException())
        );
    }

    @Override
    public void listarUsuarios(ListarUsuariosRequest request, StreamObserver<ListaUsuariosResponse> responseObserver) {
        String tipo = request.getTipoUsuario() == null ? "" : request.getTipoUsuario().trim();

        List<Usuario> resultados = tipo.isEmpty()
                ? usuarioRepo.findByActivoTrue()
                : usuarioRepo.findByTipoUsuario(tipo);

        ListaUsuariosResponse.Builder builder = ListaUsuariosResponse.newBuilder();
        resultados.forEach(u -> builder.addUsuarios(toProto(u)));

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void actualizarUsuario(ActualizarUsuarioRequest request, StreamObserver<UsuarioResponse> responseObserver) {
        usuarioRepo.findById(request.getId()).ifPresentOrElse(
                u -> {
                    if (!request.getNombre().isEmpty()) u.setNombre(request.getNombre());
                    if (!request.getApellido().isEmpty()) u.setApellido(request.getApellido());
                    if (!request.getEmail().isEmpty()) u.setEmail(request.getEmail());
                    if (!request.getTelefono().isEmpty()) u.setTelefono(request.getTelefono());

                    Usuario saved = usuarioRepo.save(u);
                    responseObserver.onNext(UsuarioResponse.newBuilder().setUsuario(toProto(saved)).build());
                    responseObserver.onCompleted();
                },
                () -> responseObserver.onError(io.grpc.Status.NOT_FOUND
                        .withDescription("Usuario no encontrado con ID: " + request.getId())
                        .asRuntimeException())
        );
    }

    @Override
    public void eliminarUsuario(IdRequest request, StreamObserver<MensajeResponse> responseObserver) {
        usuarioRepo.findById(request.getId()).ifPresentOrElse(
                u -> {
                    u.setActivo(false);
                    usuarioRepo.save(u);
                    responseObserver.onNext(MensajeResponse.newBuilder()
                            .setMensaje("Usuario eliminado correctamente.")
                            .setExito(true).build());
                    responseObserver.onCompleted();
                },
                () -> responseObserver.onError(io.grpc.Status.NOT_FOUND
                        .withDescription("Usuario no encontrado con ID: " + request.getId())
                        .asRuntimeException())
        );
    }

    @Override
    public void buscarPorEmail(EmailRequest request, StreamObserver<UsuarioResponse> responseObserver) {
        usuarioRepo.findByEmail(request.getEmail()).ifPresentOrElse(
                u -> {
                    responseObserver.onNext(UsuarioResponse.newBuilder().setUsuario(toProto(u)).build());
                    responseObserver.onCompleted();
                },
                () -> responseObserver.onError(io.grpc.Status.NOT_FOUND
                        .withDescription("Usuario no encontrado con email: " + request.getEmail())
                        .asRuntimeException())
        );
    }

    private com.mycompany.grpc.usuarios.Usuario toProto(Usuario entity) {
        return com.mycompany.grpc.usuarios.Usuario.newBuilder()
                .setId(entity.getId())
                .setNombre(entity.getNombre())
                .setApellido(entity.getApellido())
                .setEmail(entity.getEmail())
                .setTelefono(entity.getTelefono() != null ? entity.getTelefono() : "")
                .setTipoUsuario(entity.getTipoUsuario())
                .setActivo(entity.getActivo())
                .setFechaRegistro(entity.getFechaRegistro().toString())
                .build();
    }
}