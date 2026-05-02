package com.itson.ms_multas.repository;

import com.itson.ms_multas.entity.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    List<Reporte> findByUsuarioId(Long usuarioId);

    List<Reporte> findByEstatus(String estatus);

    List<Reporte> findByComercioId(Long comercioId);
}
