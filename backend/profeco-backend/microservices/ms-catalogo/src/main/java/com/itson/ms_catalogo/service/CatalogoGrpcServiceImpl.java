package com.itson.ms_catalogo.service;


import com.mycompany.grpc.catalogo.CatalogoServiceGrpc;
import com.mycompany.grpc.catalogo.Empty;
import com.mycompany.grpc.catalogo.PingResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import com.mycompany.grpc.catalogo.BuscarRequest;
import com.mycompany.grpc.catalogo.ListaProductosResponse;
import com.mycompany.grpc.catalogo.Producto;
import java.util.List;
 


  @GrpcService // Anotación clave: Levanta este servicio en el puerto 9091
  public class CatalogoGrpcServiceImpl extends CatalogoServiceGrpc.CatalogoServiceImplBase {

       @Override
      public void ping(Empty request, StreamObserver<PingResponse> responseObserver) {
          PingResponse response = PingResponse.newBuilder()
                  .setMensaje("Pong! Conectado a ms-catalogo exitosamente.")
                  .build();

          responseObserver.onNext(response);
          responseObserver.onCompleted();
      }
      
      private static final List<Producto> CATALOGO_DEMO = List.of(
              Producto.newBuilder()
                      .setId("P-001")
                      .setNombre("Leche Lala Entera 1L")
                      .setPrecio(28.50)
                      .setSupermercado("Soriana")
                      .build(),
              Producto.newBuilder()
                      .setId("P-002")
                      .setNombre("Huevo Blanco 18 pzs")
                      .setPrecio(62.90)
                      .setSupermercado("Ley")
                      .build(),
              Producto.newBuilder()
                      .setId("P-003")
                      .setNombre("Pan Bimbo Grande")
                      .setPrecio(45.00)
                      .setSupermercado("Walmart")
                      .build()
      );

      @Override
      public void buscarProductos(BuscarRequest request, StreamObserver<ListaProductosResponse> responseObserver) {
          String query = request.getQuery() == null ? "" : request.getQuery().trim().toLowerCase();

          
          List<Producto> resultado = query.isEmpty()
                  ? CATALOGO_DEMO
                  : CATALOGO_DEMO.stream()
                          .filter(p -> p.getNombre().toLowerCase().contains(query))
                          .toList();

          ListaProductosResponse response = ListaProductosResponse.newBuilder()
                  .addAllProductos(resultado)
                  .build();

          responseObserver.onNext(response);
          responseObserver.onCompleted();
      }
  }