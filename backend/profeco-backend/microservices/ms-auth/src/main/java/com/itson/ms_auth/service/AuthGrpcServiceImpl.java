package com.itson.ms_auth.service;

import com.itson.ms_auth.entity.Usuario;
import com.itson.ms_auth.repository.UsuarioRepository;
import com.mycompany.grpc.auth.*;
import io.grpc.stub.StreamObserver;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Implementación gRPC del servicio de autenticación.
 * Maneja Login, Registro, RefreshToken y Validación de tokens JWT.
 */
@GrpcService
public class AuthGrpcServiceImpl extends AuthServiceGrpc.AuthServiceImplBase {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void ping(com.mycompany.grpc.auth.Empty request,
                     StreamObserver<PingResponse> responseObserver) {
        responseObserver.onNext(PingResponse.newBuilder()
                .setMensaje("Pong! ms-auth conectado y funcionando.")
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void login(LoginRequest request, StreamObserver<AuthResponse> responseObserver) {
        String email = request.getEmail() == null ? "" : request.getEmail().trim();
        String password = request.getPassword() == null ? "" : request.getPassword();

        if (email.isEmpty() || password.isEmpty()) {
            responseObserver.onNext(AuthResponse.newBuilder()
                    .setExito(false)
                    .setMensaje("Captura email y contraseña.")
                    .build());
            responseObserver.onCompleted();
            return;
        }

        usuarioRepo.findByEmail(email).ifPresentOrElse(
                usuario -> {
                    if (!Boolean.TRUE.equals(usuario.getActivo())) {
                        responseObserver.onNext(AuthResponse.newBuilder()
                                .setExito(false)
                                .setMensaje("Usuario inactivo.")
                                .build());
                        responseObserver.onCompleted();
                        return;
                    }

                    if (!passwordEncoder.matches(password, usuario.getPassword())) {
                        responseObserver.onNext(AuthResponse.newBuilder()
                                .setExito(false)
                                .setMensaje("Contraseña incorrecta.")
                                .build());
                        responseObserver.onCompleted();
                        return;
                    }

                    String token = jwtService.generarToken(
                            usuario.getId(),
                            usuario.getEmail(),
                            usuario.getTipoUsuario(),
                            usuario.getNombre()
                    );

                    responseObserver.onNext(AuthResponse.newBuilder()
                            .setExito(true)
                            .setMensaje("Bienvenido, " + usuario.getNombre() + ".")
                            .setToken(token)
                            .setUsuario(toUsuarioInfo(usuario))
                            .build());
                    responseObserver.onCompleted();
                },
                () -> {
                    responseObserver.onNext(AuthResponse.newBuilder()
                            .setExito(false)
                            .setMensaje("No existe una cuenta con ese email.")
                            .build());
                    responseObserver.onCompleted();
                }
        );
    }

    @Override
    public void registrar(RegistrarRequest request, StreamObserver<AuthResponse> responseObserver) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT
                    .withDescription("email es obligatorio").asRuntimeException());
            return;
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT
                    .withDescription("password debe tener al menos 6 caracteres").asRuntimeException());
            return;
        }
        if (usuarioRepo.findByEmail(request.getEmail()).isPresent()) {
            responseObserver.onError(io.grpc.Status.ALREADY_EXISTS
                    .withDescription("Ya existe un usuario con ese email").asRuntimeException());
            return;
        }

        Usuario entity = new Usuario();
        entity.setNombre(request.getNombre());
        entity.setApellido(request.getApellido());
        entity.setEmail(request.getEmail().trim());
        entity.setTelefono(request.getTelefono());
        String tipo = request.getTipoUsuario() == null || request.getTipoUsuario().isBlank()
                ? "CONSUMIDOR" : request.getTipoUsuario();
        entity.setTipoUsuario(tipo);
        entity.setPassword(passwordEncoder.encode(request.getPassword()));

        Usuario saved = usuarioRepo.save(entity);

        String token = jwtService.generarToken(
                saved.getId(),
                saved.getEmail(),
                saved.getTipoUsuario(),
                saved.getNombre()
        );

        responseObserver.onNext(AuthResponse.newBuilder()
                .setExito(true)
                .setMensaje("Cuenta creada exitosamente.")
                .setToken(token)
                .setUsuario(toUsuarioInfo(saved))
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void refreshToken(RefreshTokenRequest request, StreamObserver<AuthResponse> responseObserver) {
        try {
            Claims claims = jwtService.validarToken(request.getToken());
            Long userId = Long.valueOf(claims.getSubject());

            usuarioRepo.findById(userId).ifPresentOrElse(
                    usuario -> {
                        String newToken = jwtService.generarToken(
                                usuario.getId(),
                                usuario.getEmail(),
                                usuario.getTipoUsuario(),
                                usuario.getNombre()
                        );

                        responseObserver.onNext(AuthResponse.newBuilder()
                                .setExito(true)
                                .setMensaje("Token renovado.")
                                .setToken(newToken)
                                .setUsuario(toUsuarioInfo(usuario))
                                .build());
                        responseObserver.onCompleted();
                    },
                    () -> {
                        responseObserver.onNext(AuthResponse.newBuilder()
                                .setExito(false)
                                .setMensaje("Usuario no encontrado.")
                                .build());
                        responseObserver.onCompleted();
                    }
            );
        } catch (JwtException e) {
            responseObserver.onNext(AuthResponse.newBuilder()
                    .setExito(false)
                    .setMensaje("Token inválido o expirado: " + e.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void validarToken(ValidarTokenRequest request, StreamObserver<ValidarTokenResponse> responseObserver) {
        try {
            Claims claims = jwtService.validarToken(request.getToken());

            UsuarioInfo info = UsuarioInfo.newBuilder()
                    .setId(Long.parseLong(claims.getSubject()))
                    .setEmail(claims.get("email", String.class))
                    .setTipoUsuario(claims.get("tipo_usuario", String.class))
                    .setNombre(claims.get("nombre", String.class))
                    .build();

            responseObserver.onNext(ValidarTokenResponse.newBuilder()
                    .setValido(true)
                    .setMensaje("Token válido.")
                    .setUsuario(info)
                    .build());
            responseObserver.onCompleted();
        } catch (JwtException e) {
            responseObserver.onNext(ValidarTokenResponse.newBuilder()
                    .setValido(false)
                    .setMensaje("Token inválido: " + e.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }

    private UsuarioInfo toUsuarioInfo(Usuario entity) {
        return UsuarioInfo.newBuilder()
                .setId(entity.getId())
                .setNombre(entity.getNombre())
                .setApellido(entity.getApellido())
                .setEmail(entity.getEmail())
                .setTelefono(entity.getTelefono() != null ? entity.getTelefono() : "")
                .setTipoUsuario(entity.getTipoUsuario())
                .build();
    }
}
