package com.itson.ms_catalogo.repository;

import com.itson.ms_catalogo.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByActivoTrue();

    List<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);

    List<Producto> findByCategoriaAndActivoTrue(String categoria);

    List<Producto> findByNombreContainingIgnoreCaseAndCategoriaAndActivoTrue(String nombre, String categoria);
}
