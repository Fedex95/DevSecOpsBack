package com.tienda.kpback.Repository;

import com.tienda.kpback.Entity.Cart;
import com.tienda.kpback.Entity.UsuarioEnt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUsuario(UsuarioEnt usuario);
}
