package com.itson.ms_usuarios.repository;

import com.itson.ms_usuarios.entity.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

    Optional<FcmToken> findByToken(String token);

    List<FcmToken> findByUsuarioId(Long usuarioId);

    @Query("""
        SELECT t.token
          FROM FcmToken t
         WHERE t.usuarioId IN (
            SELECT f.usuarioId FROM ComercioFavorito f WHERE f.comercioId = :comercioId
         )
        """)
    List<String> findTokensByComercioFavorito(Long comercioId);
}
