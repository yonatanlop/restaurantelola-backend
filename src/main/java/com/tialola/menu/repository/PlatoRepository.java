package com.tialola.menu.repository;

import com.tialola.menu.model.Plato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlatoRepository extends JpaRepository<Plato, Long> {
    List<Plato> findByActivoTrue();
    List<Plato> findByActivoTrueAndTipoComida(String tipoComida);
}
