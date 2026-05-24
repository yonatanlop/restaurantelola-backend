package com.tialola.inventario.repository;

import com.tialola.inventario.model.Insumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InsumoRepository extends JpaRepository<Insumo, Long> {
    List<Insumo> findByActivoTrue();
    
    @Query("SELECT i FROM Insumo i WHERE i.activo = true AND i.cantidadActual <= i.cantidadMinima")
    List<Insumo> findInsumosBajoStock();
}
