package com.tialola.caja.repository;

import com.tialola.caja.model.CierreCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CierreCajaRepository extends JpaRepository<CierreCaja, Long> {
    Optional<CierreCaja> findByFechaAndEstado(LocalDate fecha, String estado);
    List<CierreCaja> findByFechaBetweenOrderByFechaDesc(LocalDate inicio, LocalDate fin);
    List<CierreCaja> findByUsuarioIdOrderByFechaDesc(Long usuarioId);
    Optional<CierreCaja> findTopByOrderByFechaDesc();
}
