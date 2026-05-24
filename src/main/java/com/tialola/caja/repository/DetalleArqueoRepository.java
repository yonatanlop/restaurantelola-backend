package com.tialola.caja.repository;

import com.tialola.caja.model.DetalleArqueo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleArqueoRepository extends JpaRepository<DetalleArqueo, Long> {
    List<DetalleArqueo> findByCierreId(Long cierreId);
    void deleteByCierreId(Long cierreId);
}
