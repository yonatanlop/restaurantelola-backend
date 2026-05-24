package com.tialola.mesas.repository;

import com.tialola.mesas.model.TransferenciaMesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransferenciaMesaRepository extends JpaRepository<TransferenciaMesa, Long> {
    List<TransferenciaMesa> findByFechaBetweenOrderByFechaDesc(LocalDateTime inicio, LocalDateTime fin);
    List<TransferenciaMesa> findByVentaId(Long ventaId);
}
