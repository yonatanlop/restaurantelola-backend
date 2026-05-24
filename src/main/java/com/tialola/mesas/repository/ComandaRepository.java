package com.tialola.mesas.repository;

import com.tialola.mesas.model.Comanda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComandaRepository extends JpaRepository<Comanda, Long> {
    List<Comanda> findByMesaIdOrderByFechaCreacionDesc(Long mesaId);
    List<Comanda> findByEstadoOrderByFechaCreacionAsc(String estado);
    List<Comanda> findByVentaId(Long ventaId);
}
