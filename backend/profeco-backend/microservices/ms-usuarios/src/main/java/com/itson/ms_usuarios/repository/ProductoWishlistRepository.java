package com.itson.ms_usuarios.repository;

import com.itson.ms_usuarios.entity.ProductoWishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductoWishlistRepository extends JpaRepository<ProductoWishlist, Long> {

    List<ProductoWishlist> findByUsuarioId(Long usuarioId);

    @Transactional
    void deleteByUsuarioId(Long usuarioId);
}
