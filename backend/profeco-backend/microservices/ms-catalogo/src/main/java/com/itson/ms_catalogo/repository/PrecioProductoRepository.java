package com.itson.ms_catalogo.repository;

import com.itson.ms_catalogo.entity.PrecioProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PrecioProductoRepository extends JpaRepository<PrecioProducto, Long> {

    List<PrecioProducto> findByProductoId(Long productoId);

    Optional<PrecioProducto> findByProductoIdAndComercioId(Long productoId, Long comercioId);
}
