package com.tialola.inventario.repository;

import com.tialola.inventario.model.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {
    List<MovimientoInventario> findByInsumoIdOrderByFechaDesc(Long insumoId);
    List<MovimientoInventario> findByFechaBetweenOrderByFechaDesc(LocalDateTime inicio, LocalDateTime fin);
}
