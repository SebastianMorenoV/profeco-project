package com.itson.ms_usuarios.repository;

import com.itson.ms_usuarios.entity.ComercioFavorito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ComercioFavoritoRepository extends JpaRepository<ComercioFavorito, Long> {

    List<ComercioFavorito> findByUsuarioId(Long usuarioId);

    @Transactional
    void deleteByUsuarioId(Long usuarioId);
}
