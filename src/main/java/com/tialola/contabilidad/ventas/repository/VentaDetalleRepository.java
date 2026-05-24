package com.tialola.contabilidad.ventas.repository;

import com.tialola.contabilidad.ventas.model.VentaDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaDetalleRepository extends JpaRepository<VentaDetalle, Long> {
    
    List<VentaDetalle> findByVentaId(Long ventaId);
    
    @Query("SELECT vd.platoId, SUM(vd.cantidad), SUM(vd.subtotal) " +
           "FROM VentaDetalle vd " +
           "WHERE vd.venta.fecha BETWEEN :inicio AND :fin " +
           "GROUP BY vd.platoId " +
           "ORDER BY SUM(vd.cantidad) DESC")
    List<Object[]> findTopPlatosByPeriodo(@Param("inicio") LocalDateTime inicio, 
                                          @Param("fin") LocalDateTime fin);
}
