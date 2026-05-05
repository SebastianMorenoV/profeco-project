package com.itson.ms_usuarios.repository;

import com.itson.ms_usuarios.entity.ItemListaCompras;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ItemListaComprasRepository extends JpaRepository<ItemListaCompras, Long> {

    List<ItemListaCompras> findByUsuarioIdOrderByIdLocalAsc(Long usuarioId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ItemListaCompras i WHERE i.usuarioId = :usuarioId")
    void deleteByUsuarioId(@Param("usuarioId") Long usuarioId);
}
