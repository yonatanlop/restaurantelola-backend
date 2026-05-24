package com.tialola.auth.repository;

import com.tialola.auth.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsuario(String usuario);
    Optional<Usuario> findByUsuarioAndActivo(String usuario, Boolean activo);
    List<Usuario> findByRolAndActivo(String rol, Boolean activo);
}
