package com.tialola.auditoria.repository;

import com.tialola.auditoria.model.AuditoriaLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditoriaLogRepository extends JpaRepository<AuditoriaLog, Long> {
    List<AuditoriaLog> findByUsuarioIdOrderByFechaDesc(Long usuarioId);
    List<AuditoriaLog> findByEntidadAndEntidadIdOrderByFechaDesc(String entidad, Long entidadId);
    List<AuditoriaLog> findByAccionOrderByFechaDesc(String accion);
    List<AuditoriaLog> findByFechaBetweenOrderByFechaDesc(LocalDateTime inicio, LocalDateTime fin);
    
    @Query("SELECT a FROM AuditoriaLog a WHERE a.fecha BETWEEN :inicio AND :fin AND a.entidad = :entidad ORDER BY a.fecha DESC")
    List<AuditoriaLog> findByFechaAndEntidad(@Param("inicio") LocalDateTime inicio, 
                                              @Param("fin") LocalDateTime fin, 
                                              @Param("entidad") String entidad);
}
