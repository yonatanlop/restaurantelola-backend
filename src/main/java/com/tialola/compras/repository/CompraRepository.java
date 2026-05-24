package com.tialola.compras.repository;

import com.tialola.compras.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {
    List<Compra> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
    List<Compra> findByProveedorId(Long proveedorId);
}
