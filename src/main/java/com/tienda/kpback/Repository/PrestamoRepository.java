package com.tienda.kpback.Repository;

import com.tienda.kpback.Entity.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID; 

public interface PrestamoRepository extends JpaRepository<Prestamo, UUID> { 
    List<Prestamo> findByUsuarioId(UUID usuarioId);  
}
