package com.tialola.contabilidad.ventas.repository;

import com.tialola.contabilidad.ventas.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
    Optional<Venta> findTopByOrderByFechaDesc();
}
