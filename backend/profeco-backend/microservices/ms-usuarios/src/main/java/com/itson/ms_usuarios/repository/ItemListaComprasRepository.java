package com.itson.ms_usuarios.repository;

import com.itson.ms_usuarios.entity.ItemListaCompras;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ItemListaComprasRepository extends JpaRepository<ItemListaCompras, Long> {

    List<ItemListaCompras> findByUsuarioIdOrderByIdLocalAsc(Long usuarioId);

    @Transactional
    void deleteByUsuarioId(Long usuarioId);
}
