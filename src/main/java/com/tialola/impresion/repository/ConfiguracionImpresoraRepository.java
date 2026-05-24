package com.tialola.impresion.repository;

import com.tialola.impresion.model.ConfiguracionImpresora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfiguracionImpresoraRepository extends JpaRepository<ConfiguracionImpresora, Long> {
    List<ConfiguracionImpresora> findByActivoTrue();
    Optional<ConfiguracionImpresora> findByEsPredeterminadaTrueAndActivoTrue();
}
