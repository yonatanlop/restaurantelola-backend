package com.tialola.mesas.repository;

import com.tialola.mesas.model.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Long> {
    List<Mesa> findByActivaTrueOrderByNumeroAsc();
    List<Mesa> findByEstadoAndActivaTrue(String estado);
    List<Mesa> findByUbicacionAndActivaTrue(String ubicacion);
    Optional<Mesa> findByNumero(String numero);
}
