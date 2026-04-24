package com.itson.gateway_comercio.controller;

import com.mycompany.grpc.catalogo.CatalogoServiceGrpc;
import com.mycompany.grpc.comercio.ComercioServiceGrpc;

import com.mycompany.grpc.comercio.Empty;
import com.mycompany.grpc.comercio.PingResponse;
import com.mycompany.grpc.ofertas.OfertasServiceGrpc;
import com.mycompany.grpc.resenias.ReseniasServiceGrpc;
import com.mycompany.grpc.usuarios.UsuariosServiceGrpc;
import java.util.LinkedHashMap;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class ComercioHealthController {

    // Debe coincidir EXACTAMENTE con el nombre en application.properties
    @GrpcClient("ms-comercio")
    private ComercioServiceGrpc.ComercioServiceBlockingStub comercioStub;

    @GrpcClient("ms-catalogo")
    private CatalogoServiceGrpc.CatalogoServiceBlockingStub catalogoStub;

    @GrpcClient("ms-usuarios")
    private UsuariosServiceGrpc.UsuariosServiceBlockingStub usuariosStub;

    @GrpcClient("ms-resenias")
    private ReseniasServiceGrpc.ReseniasServiceBlockingStub reseniasStub;

    @GrpcClient("ms-ofertas")
    private OfertasServiceGrpc.OfertasServiceBlockingStub ofertasStub;

    @GetMapping("/catalogo")
    public ResponseEntity<?> pingCatalogo() {
        try {
            // Creamos la petición vacía
            com.mycompany.grpc.catalogo.Empty request = com.mycompany.grpc.catalogo.Empty.newBuilder().build();

            // Hacemos la llamada mágica por gRPC a ms-comercio
            com.mycompany.grpc.catalogo.PingResponse response = catalogoStub.ping(request);

            // Respondemos al navegador o Postman en formato JSON
            return ResponseEntity.ok(Map.of(
                    "status", "OK",
                    "mensaje", response.getMensaje()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "ERROR",
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/comercio")
    public ResponseEntity<?> pingComercio() {
        try {
            // Creamos la petición vacía
            Empty request = Empty.newBuilder().build();

            // Hacemos la llamada mágica por gRPC a ms-comercio
            PingResponse response = comercioStub.ping(request);

            // Respondemos al navegador o Postman en formato JSON
            return ResponseEntity.ok(Map.of(
                    "status", "OK",
                    "mensaje", response.getMensaje()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "ERROR",
                    "error", e.getMessage()
            ));
        }
    }

    // 3. USUARIOS (Puerto 9096)
    @GetMapping("/usuarios")
    public ResponseEntity<?> pingUsuarios() {
        try {
            com.mycompany.grpc.usuarios.Empty request = com.mycompany.grpc.usuarios.Empty.newBuilder().build();
            com.mycompany.grpc.usuarios.PingResponse response = usuariosStub.ping(request);
            return ResponseEntity.ok(Map.of("status", "OK", "mensaje", response.getMensaje()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("status", "ERROR", "error", e.getMessage()));
        }
    }

    // 4. RESEÑAS
    @GetMapping("/resenias")
    public ResponseEntity<?> pingResenias() {
        try {
            com.mycompany.grpc.resenias.Empty request = com.mycompany.grpc.resenias.Empty.newBuilder().build();
            com.mycompany.grpc.resenias.PingResponse response = reseniasStub.ping(request);
            return ResponseEntity.ok(Map.of("status", "OK", "mensaje", response.getMensaje()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("status", "ERROR", "error", e.getMessage()));
        }
    }

    // 5. OFERTAS
    @GetMapping("/ofertas")
    public ResponseEntity<?> pingOfertas() {
        try {
            com.mycompany.grpc.ofertas.Empty request = com.mycompany.grpc.ofertas.Empty.newBuilder().build();
            com.mycompany.grpc.ofertas.PingResponse response = ofertasStub.ping(request);
            return ResponseEntity.ok(Map.of("status", "OK", "mensaje", response.getMensaje()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("status", "ERROR", "error", e.getMessage()));
        }
    }

    // 6. CHECK ALL (Consolidado)
    @GetMapping("/check-all")
    public ResponseEntity<?> checkAll() {
        Map<String, Object> report = new LinkedHashMap<>();

        report.put("catalogo", getStatus(() -> catalogoStub.ping(com.mycompany.grpc.catalogo.Empty.newBuilder().build()).getMensaje()));
        report.put("comercio", getStatus(() -> comercioStub.ping(Empty.newBuilder().build()).getMensaje()));
        report.put("usuarios", getStatus(() -> usuariosStub.ping(com.mycompany.grpc.usuarios.Empty.newBuilder().build()).getMensaje()));
        report.put("resenias", getStatus(() -> reseniasStub.ping(com.mycompany.grpc.resenias.Empty.newBuilder().build()).getMensaje()));
        report.put("ofertas", getStatus(() -> ofertasStub.ping(com.mycompany.grpc.ofertas.Empty.newBuilder().build()).getMensaje()));

        return ResponseEntity.ok(report);
    }

    // Método auxiliar para el check-all
    private String getStatus(java.util.function.Supplier<String> call) {
        try {
            return call.get();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
