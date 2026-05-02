package com.itson.ms_ofertas.repository;

import com.itson.ms_ofertas.entity.Oferta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OfertaRepository extends JpaRepository<Oferta, Long> {

    List<Oferta> findByActivaTrue();

    List<Oferta> findByComercioId(Long comercioId);

    List<Oferta> findByComercioIdAndActivaTrue(Long comercioId);
}
