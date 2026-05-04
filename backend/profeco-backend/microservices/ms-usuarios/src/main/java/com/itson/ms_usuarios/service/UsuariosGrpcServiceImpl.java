package com.itson.ms_usuarios.service;

import com.itson.ms_usuarios.entity.ComercioFavorito;
import com.itson.ms_usuarios.entity.FcmToken;
import com.itson.ms_usuarios.entity.ProductoWishlist;
import com.itson.ms_usuarios.entity.Usuario;
import com.itson.ms_usuarios.repository.ComercioFavoritoRepository;
import com.itson.ms_usuarios.repository.FcmTokenRepository;
import com.itson.ms_usuarios.repository.ItemListaComprasRepository;
import com.itson.ms_usuarios.repository.ProductoWishlistRepository;
import com.itson.ms_usuarios.repository.UsuarioRepository;

import com.mycompany.grpc.usuarios.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@GrpcService
public class UsuariosGrpcServiceImpl extends UsuariosServiceGrpc.UsuariosServiceImplBase {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private FcmTokenRepository fcmTokenRepo;

    @Autowired
    private ComercioFavoritoRepository favoritoRepo;

    @Autowired
    private ProductoWishlistRepository wishlistRepo;

    @Autowired
    private ItemListaComprasRepository listaComprasRepo;

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

    // ==================== FCM TOKENS ====================

    @Override
    public void registrarFcmToken(RegistrarFcmTokenRequest request, StreamObserver<MensajeResponse> responseObserver) {
        if (request.getToken() == null || request.getToken().isBlank()) {
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT
                    .withDescription("token vacío").asRuntimeException());
            return;
        }
        FcmToken entity = fcmTokenRepo.findByToken(request.getToken()).orElse(new FcmToken());
        entity.setToken(request.getToken());
        entity.setUsuarioId(request.getUsuarioId());
        entity.setPlataforma(request.getPlataforma() != null && !request.getPlataforma().isBlank()
                ? request.getPlataforma() : "ANDROID");
        entity.setFechaActualizacion(LocalDateTime.now());
        fcmTokenRepo.save(entity);

        responseObserver.onNext(MensajeResponse.newBuilder()
                .setMensaje("Token FCM registrado.").setExito(true).build());
        responseObserver.onCompleted();
    }

    @Override
    public void listarFcmTokensPorComercio(IdRequest request, StreamObserver<ListaTokensResponse> responseObserver) {
        List<String> tokens = fcmTokenRepo.findTokensByComercioFavorito(request.getId());
        responseObserver.onNext(ListaTokensResponse.newBuilder()
                .addAllTokens(tokens).build());
        responseObserver.onCompleted();
    }

    // ==================== COMERCIOS FAVORITOS ====================

    @Override
    public void listarComerciosFavoritos(IdRequest request, StreamObserver<ListaIdsResponse> responseObserver) {
        List<Long> ids = favoritoRepo.findByUsuarioId(request.getId()).stream()
                .map(ComercioFavorito::getComercioId).toList();
        responseObserver.onNext(ListaIdsResponse.newBuilder()
                .setUsuarioId(request.getId())
                .addAllIds(ids).build());
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void syncComerciosFavoritos(SyncIdsRequest request, StreamObserver<ListaIdsResponse> responseObserver) {
        favoritoRepo.deleteByUsuarioId(request.getUsuarioId());
        List<ComercioFavorito> nuevos = new ArrayList<>();
        for (Long comercioId : request.getIdsList()) {
            nuevos.add(new ComercioFavorito(request.getUsuarioId(), comercioId));
        }
        favoritoRepo.saveAll(nuevos);

        responseObserver.onNext(ListaIdsResponse.newBuilder()
                .setUsuarioId(request.getUsuarioId())
                .addAllIds(request.getIdsList()).build());
        responseObserver.onCompleted();
    }

    // ==================== WISHLIST ====================

    @Override
    public void listarWishlist(IdRequest request, StreamObserver<ListaIdsResponse> responseObserver) {
        List<Long> ids = wishlistRepo.findByUsuarioId(request.getId()).stream()
                .map(ProductoWishlist::getProductoId).toList();
        responseObserver.onNext(ListaIdsResponse.newBuilder()
                .setUsuarioId(request.getId())
                .addAllIds(ids).build());
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void syncWishlist(SyncIdsRequest request, StreamObserver<ListaIdsResponse> responseObserver) {
        wishlistRepo.deleteByUsuarioId(request.getUsuarioId());
        List<ProductoWishlist> nuevos = new ArrayList<>();
        for (Long productoId : request.getIdsList()) {
            nuevos.add(new ProductoWishlist(request.getUsuarioId(), productoId));
        }
        wishlistRepo.saveAll(nuevos);

        responseObserver.onNext(ListaIdsResponse.newBuilder()
                .setUsuarioId(request.getUsuarioId())
                .addAllIds(request.getIdsList()).build());
        responseObserver.onCompleted();
    }

    // ==================== LISTA DE COMPRAS ====================

    @Override
    public void listarListaCompras(IdRequest request, StreamObserver<ListaItemsComprasResponse> responseObserver) {
        ListaItemsComprasResponse.Builder builder = ListaItemsComprasResponse.newBuilder()
                .setUsuarioId(request.getId());
        listaComprasRepo.findByUsuarioIdOrderByIdLocalAsc(request.getId()).forEach(item ->
                builder.addItems(ItemListaCompras.newBuilder()
                        .setIdLocal(item.getIdLocal())
                        .setNombre(item.getNombre())
                        .setMarcado(item.getMarcado())
                        .build()));
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void syncListaCompras(SyncListaComprasRequest request, StreamObserver<ListaItemsComprasResponse> responseObserver) {
        listaComprasRepo.deleteByUsuarioId(request.getUsuarioId());
        List<com.itson.ms_usuarios.entity.ItemListaCompras> nuevos = new ArrayList<>();
        for (ItemListaCompras item : request.getItemsList()) {
            nuevos.add(new com.itson.ms_usuarios.entity.ItemListaCompras(
                    request.getUsuarioId(),
                    item.getIdLocal(),
                    item.getNombre(),
                    item.getMarcado()
            ));
        }
        listaComprasRepo.saveAll(nuevos);

        ListaItemsComprasResponse.Builder builder = ListaItemsComprasResponse.newBuilder()
                .setUsuarioId(request.getUsuarioId());
        request.getItemsList().forEach(builder::addItems);
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }
}