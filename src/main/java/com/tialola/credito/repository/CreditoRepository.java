package com.tialola.credito.repository;

import com.tialola.credito.model.Credito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CreditoRepository extends JpaRepository<Credito, Long> {
    List<Credito> findByClienteIdOrderByFechaPedidoDesc(Long clienteId);
    List<Credito> findByPagadoFalseOrderByFechaPedidoAsc();
    List<Credito> findByClienteIdAndPagadoFalseOrderByFechaPedidoAsc(Long clienteId);
    
    @Query("SELECT SUM(c.valorPedido) FROM Credito c WHERE c.clienteId = :clienteId AND c.pagado = false")
    BigDecimal calcularDeudaTotal(@Param("clienteId") Long clienteId);
}
