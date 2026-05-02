package com.itson.ms_comercio.repository;

import com.itson.ms_comercio.entity.Comercio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComercioRepository extends JpaRepository<Comercio, Long> {

    List<Comercio> findByActivoTrue();

    List<Comercio> findByCiudadAndActivoTrue(String ciudad);

    List<Comercio> findByTipoComercioAndActivoTrue(String tipoComercio);

    List<Comercio> findByCiudadAndTipoComercioAndActivoTrue(String ciudad, String tipoComercio);

    List<Comercio> findByNombreComercialContainingIgnoreCaseAndActivoTrue(String nombre);
}
