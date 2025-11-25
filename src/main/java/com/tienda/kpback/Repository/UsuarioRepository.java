package com.tienda.kpback.Repository;

import com.tienda.kpback.Entity.UsuarioEnt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEnt, UUID> {
    Optional<UsuarioEnt> findByEmail(String email);

    @Query("SELECT u FROM UsuarioEnt u WHERE u.email = :email AND u.pass = :pass")
    Optional<UsuarioEnt> findByEmailAndPass(@Param("email") String email, @Param("pass") String pass);
}
