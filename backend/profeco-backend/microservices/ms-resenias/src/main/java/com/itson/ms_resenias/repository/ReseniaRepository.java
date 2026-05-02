package com.itson.ms_resenias.repository;

import com.itson.ms_resenias.entity.Resenia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ReseniaRepository extends JpaRepository<Resenia, Long> {

    List<Resenia> findByComercioId(Long comercioId);

    @Query("SELECT AVG(r.calificacion) FROM Resenia r WHERE r.comercioId = ?1")
    Double findPromedioByComercioId(Long comercioId);

    @Query("SELECT COUNT(r) FROM Resenia r WHERE r.comercioId = ?1")
    Long countByComercioId(Long comercioId);
}
