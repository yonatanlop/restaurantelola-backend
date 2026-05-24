package com.tialola.nomina.repository;

import com.tialola.nomina.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    List<Empleado> findByActivoTrue();
    Optional<Empleado> findByDocumento(String documento);
    List<Empleado> findByPuesto(String puesto);
}
