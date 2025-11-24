package com.tienda.kpback.Repository;

import com.tienda.kpback.Entity.UsuarioEnt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;  

public interface UsuarioRepository extends JpaRepository<UsuarioEnt, UUID> {
    Optional<UsuarioEnt> findByUsuario(String usuario);
    Optional<UsuarioEnt> findByEmail(String email); 
}
