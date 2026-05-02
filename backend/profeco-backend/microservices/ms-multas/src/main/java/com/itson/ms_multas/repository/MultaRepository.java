package com.itson.ms_multas.repository;

import com.itson.ms_multas.entity.Multa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MultaRepository extends JpaRepository<Multa, Long> {

    List<Multa> findByComercioId(Long comercioId);

    List<Multa> findByEstatus(String estatus);
}
