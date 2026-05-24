package com.tialola.credito.repository;

import com.tialola.credito.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    List<Cliente> findByActivoTrueOrderByNombreAsc();
    List<Cliente> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);
}
