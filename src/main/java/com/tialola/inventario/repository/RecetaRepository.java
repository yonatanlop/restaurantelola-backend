package com.tialola.inventario.repository;

import com.tialola.inventario.model.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, Long> {
    List<Receta> findByPlatoId(Long platoId);
    List<Receta> findByInsumoId(Long insumoId);
    void deleteByPlatoIdAndInsumoId(Long platoId, Long insumoId);
}
