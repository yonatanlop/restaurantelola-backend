package com.tialola.contabilidad.ventas.repository;

import com.tialola.contabilidad.ventas.model.PropinaVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PropinaVentaRepository extends JpaRepository<PropinaVenta, Long> {
    Optional<PropinaVenta> findByVentaId(Long ventaId);
}

