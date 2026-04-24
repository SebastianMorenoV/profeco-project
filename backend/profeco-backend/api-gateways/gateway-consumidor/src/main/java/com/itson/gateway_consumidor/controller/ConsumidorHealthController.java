package com.itson.gateway_consumidor.controller;

import com.mycompany.grpc.catalogo.CatalogoServiceGrpc;

import com.mycompany.grpc.multas.MultasServiceGrpc;
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
public class ConsumidorHealthController {

    // Debe coincidir EXACTAMENTE con el nombre en application.properties
    @GrpcClient("ms-catalogo")
    private CatalogoServiceGrpc.CatalogoServiceBlockingStub catalogoStub;

    @GrpcClient("ms-usuarios")
    private UsuariosServiceGrpc.UsuariosServiceBlockingStub usuariosStub;

    @GrpcClient("ms-resenias")
    private ReseniasServiceGrpc.ReseniasServiceBlockingStub reseniasStub;

    @GrpcClient("ms-multas")
    private MultasServiceGrpc.MultasServiceBlockingStub multasStub;

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
    @GetMapping("/multas")
    public ResponseEntity<?> pingMultas() {
        try {
            com.mycompany.grpc.multas.Empty request = com.mycompany.grpc.multas.Empty.newBuilder().build();
            com.mycompany.grpc.multas.PingResponse response = multasStub.ping(request);
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
        report.put("usuarios", getStatus(() -> usuariosStub.ping(com.mycompany.grpc.usuarios.Empty.newBuilder().build()).getMensaje()));
        report.put("resenias", getStatus(() -> reseniasStub.ping(com.mycompany.grpc.resenias.Empty.newBuilder().build()).getMensaje()));
        report.put("multas", getStatus(() -> multasStub.ping(com.mycompany.grpc.multas.Empty.newBuilder().build()).getMensaje()));

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
