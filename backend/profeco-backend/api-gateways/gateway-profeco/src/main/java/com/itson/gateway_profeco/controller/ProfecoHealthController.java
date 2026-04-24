package com.itson.gateway_profeco.controller;

import com.mycompany.grpc.multas.MultasServiceGrpc;

import java.util.LinkedHashMap;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class ProfecoHealthController {

    // Debe coincidir EXACTAMENTE con el nombre en application.properties
    @GrpcClient("ms-multas")
    private MultasServiceGrpc.MultasServiceBlockingStub multasStub;

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
