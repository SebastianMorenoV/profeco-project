package com.itson.ms_auth.controller;

import com.itson.ms_auth.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controlador REST que expone el JWKS (JSON Web Key Set) para que Envoy
 * pueda obtener la clave pública y validar los tokens JWT sin consultar a ms-auth.
 * <p>
 * Este es un endpoint HTTP/1.1 estándar (no gRPC), accesible en el puerto HTTP del servidor.
 */
@RestController
public class JwksController {

    @Autowired
    private JwtService jwtService;

    @GetMapping(value = "/.well-known/jwks.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> jwks() {
        return jwtService.getJwks();
    }
}
