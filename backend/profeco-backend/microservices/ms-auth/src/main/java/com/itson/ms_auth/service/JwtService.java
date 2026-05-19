package com.itson.ms_auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

/**
 * Servicio responsable de generar y validar JSON Web Tokens (JWT) usando RSA-256.
 * <p>
 * Genera un par de claves RSA al inicio de la aplicación.
 * La clave privada se usa para firmar tokens.
 * La clave pública se expone vía JWKS para que Envoy pueda validar tokens sin pasar por ms-auth.
 */
@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);
    private static final String ISSUER = "profeco-auth-service";
    private static final String KEY_ID = "profeco-key-1";

    @Value("${jwt.expiration.hours:24}")
    private int expirationHours;

    private KeyPair keyPair;

    @PostConstruct
    public void init() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            this.keyPair = generator.generateKeyPair();
            log.info("✅ RSA-2048 key pair generated for JWT signing (kid={})", KEY_ID);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo generar el par de claves RSA", e);
        }
    }

    /**
     * Genera un JWT firmado con RS256 para el usuario especificado.
     */
    public String generarToken(Long userId, String email, String tipoUsuario, String nombre) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + (long) expirationHours * 3600 * 1000);

        return Jwts.builder()
                .header().keyId(KEY_ID).and()
                .issuer(ISSUER)
                .subject(String.valueOf(userId))
                .claim("email", email)
                .claim("tipo_usuario", tipoUsuario)
                .claim("nombre", nombre)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(keyPair.getPrivate(), Jwts.SIG.RS256)
                .compact();
    }

    /**
     * Valida un JWT y retorna los claims. Lanza JwtException si el token es inválido.
     */
    public Claims validarToken(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(keyPair.getPublic())
                .requireIssuer(ISSUER)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Retorna el JWKS (JSON Web Key Set) con la clave pública en formato estándar.
     * Envoy consume este endpoint para validar los tokens sin consultar a ms-auth.
     */
    public Map<String, Object> getJwks() {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        // Codificar modulus y exponent en Base64 URL-safe (sin padding)
        String n = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(publicKey.getModulus().toByteArray());
        String e = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(publicKey.getPublicExponent().toByteArray());

        Map<String, Object> key = Map.of(
                "kty", "RSA",
                "alg", "RS256",
                "use", "sig",
                "kid", KEY_ID,
                "n", n,
                "e", e
        );

        return Map.of("keys", java.util.List.of(key));
    }

    public String getKeyId() {
        return KEY_ID;
    }

    public String getIssuer() {
        return ISSUER;
    }
}
