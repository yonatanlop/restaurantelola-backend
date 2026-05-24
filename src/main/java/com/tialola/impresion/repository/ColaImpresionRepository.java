package com.tialola.impresion.repository;

import com.tialola.impresion.model.ColaImpresion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColaImpresionRepository extends JpaRepository<ColaImpresion, Long> {
    List<ColaImpresion> findByEstadoOrderByFechaCreacionAsc(String estado);
    List<ColaImpresion> findByTipoDocumentoAndReferenciaId(String tipoDocumento, Long referenciaId);
}
