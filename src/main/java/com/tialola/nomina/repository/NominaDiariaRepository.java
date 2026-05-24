package com.tialola.nomina.repository;

import com.tialola.nomina.model.NominaDiaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface NominaDiariaRepository extends JpaRepository<NominaDiaria, Long> {
    List<NominaDiaria> findByFecha(LocalDate fecha);
    List<NominaDiaria> findByFechaBetween(LocalDate inicio, LocalDate fin);
    List<NominaDiaria> findByEmpleadoIdAndFechaBetween(Long empleadoId, LocalDate inicio, LocalDate fin);
    Optional<NominaDiaria> findByEmpleadoIdAndFecha(Long empleadoId, LocalDate fecha);
    List<NominaDiaria> findByEstado(String estado);
    
    @Query("SELECT SUM(n.monto) FROM NominaDiaria n WHERE n.fecha = :fecha")
    BigDecimal calcularTotalDia(@Param("fecha") LocalDate fecha);
    
    @Query("SELECT SUM(n.monto) FROM NominaDiaria n WHERE n.fecha BETWEEN :inicio AND :fin")
    BigDecimal calcularTotalPeriodo(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
}
