package com.tialola.contabilidad.repository;

import com.tialola.contabilidad.model.MovimientoCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoCajaRepository extends JpaRepository<MovimientoCaja, Long> {
    List<MovimientoCaja> findByFechaBetweenOrderByFechaDesc(LocalDateTime inicio, LocalDateTime fin);
    List<MovimientoCaja> findByTipoAndFechaBetween(String tipo, LocalDateTime inicio, LocalDateTime fin);
    List<MovimientoCaja> findByConceptoAndFechaBetween(String concepto, LocalDateTime inicio, LocalDateTime fin);
    
    @Query("SELECT SUM(m.monto) FROM MovimientoCaja m WHERE m.tipo = :tipo AND m.fecha BETWEEN :inicio AND :fin")
    BigDecimal calcularTotalPorTipo(@Param("tipo") String tipo, @Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    @Query("SELECT SUM(CASE WHEN m.tipo = 'INGRESO' THEN m.monto ELSE -m.monto END) FROM MovimientoCaja m WHERE m.fecha BETWEEN :inicio AND :fin")
    BigDecimal calcularSaldo(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
}
