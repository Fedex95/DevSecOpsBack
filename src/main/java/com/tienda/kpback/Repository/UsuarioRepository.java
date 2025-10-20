package com.tienda.kpback.Repository;

import com.tienda.kpback.Entity.UsuarioEnt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioEnt, Long> {
    Optional<UsuarioEnt> findByUsuario(String usuario);
}
