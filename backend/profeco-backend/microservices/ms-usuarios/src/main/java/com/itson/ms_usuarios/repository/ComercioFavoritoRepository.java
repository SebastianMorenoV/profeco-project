package com.itson.ms_usuarios.repository;

import com.itson.ms_usuarios.entity.ComercioFavorito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ComercioFavoritoRepository extends JpaRepository<ComercioFavorito, Long> {

    List<ComercioFavorito> findByUsuarioId(Long usuarioId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ComercioFavorito c WHERE c.usuarioId = :usuarioId")
    void deleteByUsuarioId(@Param("usuarioId") Long usuarioId);
}
