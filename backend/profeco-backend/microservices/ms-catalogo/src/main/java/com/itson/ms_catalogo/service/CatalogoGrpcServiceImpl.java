package com.itson.ms_catalogo.service;

import com.itson.ms_catalogo.entity.Producto;
import com.itson.ms_catalogo.entity.PrecioProducto;
import com.itson.ms_catalogo.repository.ProductoRepository;
import com.itson.ms_catalogo.repository.PrecioProductoRepository;

import com.mycompany.grpc.catalogo.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

@GrpcService
public class CatalogoGrpcServiceImpl extends CatalogoServiceGrpc.CatalogoServiceImplBase {

    @Autowired
    private ProductoRepository productoRepo;

    @Autowired
    private PrecioProductoRepository precioRepo;

    // ==================== PING ====================
    @Override
    public void ping(Empty request, StreamObserver<PingResponse> responseObserver) {
        responseObserver.onNext(PingResponse.newBuilder()
                .setMensaje("Pong! ms-catalogo conectado y funcionando.")
                .build());
        responseObserver.onCompleted();
    }

    // ==================== PRODUCTOS ====================

    @Override
    public void crearProducto(CrearProductoRequest request, StreamObserver<ProductoResponse> responseObserver) {
        Producto entity = new Producto();
        entity.setNombre(request.getNombre());
        entity.setDescripcion(request.getDescripcion());
        entity.setMarca(request.getMarca());
        entity.setCategoria(request.getCategoria());
        entity.setCodigoBarras(request.getCodigoBarras());
        entity.setUnidadMedida(request.getUnidadMedida());

        Producto saved = productoRepo.save(entity);

        responseObserver.onNext(ProductoResponse.newBuilder()
                .setProducto(toProtoProducto(saved))
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void obtenerProducto(IdRequest request, StreamObserver<ProductoResponse> responseObserver) {
        productoRepo.findById(request.getId()).ifPresentOrElse(
                p -> {
                    responseObserver.onNext(ProductoResponse.newBuilder()
                            .setProducto(toProtoProducto(p))
                            .build());
                    responseObserver.onCompleted();
                },
                () -> {
                    responseObserver.onError(io.grpc.Status.NOT_FOUND
                            .withDescription("Producto no encontrado con ID: " + request.getId())
                            .asRuntimeException());
                }
        );
    }

    @Override
    public void buscarProductos(BuscarProductosRequest request, StreamObserver<ListaProductosResponse> responseObserver) {
        String query = request.getQuery() == null ? "" : request.getQuery().trim();
        String categoria = request.getCategoria() == null ? "" : request.getCategoria().trim();

        List<Producto> resultados;

        if (!query.isEmpty() && !categoria.isEmpty()) {
            resultados = productoRepo.findByNombreContainingIgnoreCaseAndCategoriaAndActivoTrue(query, categoria);
        } else if (!query.isEmpty()) {
            resultados = productoRepo.findByNombreContainingIgnoreCaseAndActivoTrue(query);
        } else if (!categoria.isEmpty()) {
            resultados = productoRepo.findByCategoriaAndActivoTrue(categoria);
        } else {
            resultados = productoRepo.findByActivoTrue();
        }

        ListaProductosResponse.Builder builder = ListaProductosResponse.newBuilder();
        resultados.forEach(p -> builder.addProductos(toProtoProducto(p)));

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void actualizarProducto(ActualizarProductoRequest request, StreamObserver<ProductoResponse> responseObserver) {
        productoRepo.findById(request.getId()).ifPresentOrElse(
                p -> {
                    if (!request.getNombre().isEmpty()) p.setNombre(request.getNombre());
                    if (!request.getDescripcion().isEmpty()) p.setDescripcion(request.getDescripcion());
                    if (!request.getMarca().isEmpty()) p.setMarca(request.getMarca());
                    if (!request.getCategoria().isEmpty()) p.setCategoria(request.getCategoria());
                    if (!request.getCodigoBarras().isEmpty()) p.setCodigoBarras(request.getCodigoBarras());
                    if (!request.getUnidadMedida().isEmpty()) p.setUnidadMedida(request.getUnidadMedida());

                    Producto saved = productoRepo.save(p);
                    responseObserver.onNext(ProductoResponse.newBuilder()
                            .setProducto(toProtoProducto(saved))
                            .build());
                    responseObserver.onCompleted();
                },
                () -> {
                    responseObserver.onError(io.grpc.Status.NOT_FOUND
                            .withDescription("Producto no encontrado con ID: " + request.getId())
                            .asRuntimeException());
                }
        );
    }

    @Override
    public void eliminarProducto(IdRequest request, StreamObserver<MensajeResponse> responseObserver) {
        productoRepo.findById(request.getId()).ifPresentOrElse(
                p -> {
                    p.setActivo(false);
                    productoRepo.save(p);
                    responseObserver.onNext(MensajeResponse.newBuilder()
                            .setMensaje("Producto eliminado correctamente.")
                            .setExito(true)
                            .build());
                    responseObserver.onCompleted();
                },
                () -> {
                    responseObserver.onError(io.grpc.Status.NOT_FOUND
                            .withDescription("Producto no encontrado con ID: " + request.getId())
                            .asRuntimeException());
                }
        );
    }

    // ==================== PRECIOS ====================

    @Override
    public void registrarPrecio(RegistrarPrecioRequest request, StreamObserver<PrecioResponse> responseObserver) {
        // Upsert: si ya existe un precio para ese producto+comercio, actualizarlo
        PrecioProducto entity = precioRepo
                .findByProductoIdAndComercioId(request.getProductoId(), request.getComercioId())
                .orElse(new PrecioProducto());

        entity.setProductoId(request.getProductoId());
        entity.setComercioId(request.getComercioId());
        entity.setPrecio(BigDecimal.valueOf(request.getPrecio()));
        entity.setFechaReporte(java.time.LocalDateTime.now());

        PrecioProducto saved = precioRepo.save(entity);

        responseObserver.onNext(PrecioResponse.newBuilder()
                .setPrecio(toProtoPrecio(saved))
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void obtenerPreciosProducto(IdRequest request, StreamObserver<ListaPreciosResponse> responseObserver) {
        List<PrecioProducto> precios = precioRepo.findByProductoId(request.getId());

        ListaPreciosResponse.Builder builder = ListaPreciosResponse.newBuilder();
        precios.forEach(p -> builder.addPrecios(toProtoPrecio(p)));

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void actualizarPrecio(ActualizarPrecioRequest request, StreamObserver<PrecioResponse> responseObserver) {
        precioRepo.findById(request.getId()).ifPresentOrElse(
                p -> {
                    p.setPrecio(BigDecimal.valueOf(request.getPrecio()));
                    p.setFechaReporte(java.time.LocalDateTime.now());
                    PrecioProducto saved = precioRepo.save(p);
                    responseObserver.onNext(PrecioResponse.newBuilder()
                            .setPrecio(toProtoPrecio(saved))
                            .build());
                    responseObserver.onCompleted();
                },
                () -> {
                    responseObserver.onError(io.grpc.Status.NOT_FOUND
                            .withDescription("Precio no encontrado con ID: " + request.getId())
                            .asRuntimeException());
                }
        );
    }

    // ==================== MAPPERS ====================

    private com.mycompany.grpc.catalogo.Producto toProtoProducto(Producto entity) {
        return com.mycompany.grpc.catalogo.Producto.newBuilder()
                .setId(entity.getId())
                .setNombre(entity.getNombre())
                .setDescripcion(entity.getDescripcion() != null ? entity.getDescripcion() : "")
                .setMarca(entity.getMarca() != null ? entity.getMarca() : "")
                .setCategoria(entity.getCategoria())
                .setCodigoBarras(entity.getCodigoBarras() != null ? entity.getCodigoBarras() : "")
                .setUnidadMedida(entity.getUnidadMedida() != null ? entity.getUnidadMedida() : "")
                .setActivo(entity.getActivo())
                .setFechaCreacion(entity.getFechaCreacion().toString())
                .build();
    }

    private com.mycompany.grpc.catalogo.PrecioProducto toProtoPrecio(PrecioProducto entity) {
        return com.mycompany.grpc.catalogo.PrecioProducto.newBuilder()
                .setId(entity.getId())
                .setProductoId(entity.getProductoId())
                .setComercioId(entity.getComercioId())
                .setPrecio(entity.getPrecio().doubleValue())
                .setFechaReporte(entity.getFechaReporte().toString())
                .build();
    }
}