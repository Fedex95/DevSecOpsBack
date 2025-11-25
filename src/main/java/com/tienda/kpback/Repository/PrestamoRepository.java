package com.tienda.kpback.Repository;

import com.tienda.kpback.Entity.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID; 
import org.springframework.data.repository.query.Param;

public interface PrestamoRepository extends JpaRepository<Prestamo, UUID> { 
    List<Prestamo> findByUsuarioId(UUID usuarioId); 
    @Query("SELECT p FROM Prestamo p JOIN FETCH p.usuario JOIN FETCH p.detallesPrestamo dp JOIN FETCH dp.libro WHERE p.usuario.id = :userId")
    List<Prestamo> findByUsuarioIdWithUsuario(@Param("userId") UUID userId);
    @Query("SELECT p FROM Prestamo p JOIN FETCH p.usuario JOIN FETCH p.detallesPrestamo dp JOIN FETCH dp.libro WHERE p.usuario.id = :userId AND p.estado = :estado")
    List<Prestamo> findByUsuarioIdAndEstadoWithUsuario(@Param("userId") UUID userId, @Param("estado") Prestamo.Estado estado);
    @Query("SELECT p FROM Prestamo p JOIN FETCH p.usuario JOIN FETCH p.detallesPrestamo dp JOIN FETCH dp.libro")
    List<Prestamo> findAllWithUsuario();
    List<Prestamo> findByUsuarioIdAndEstado(UUID usuarioId, Prestamo.Estado estado);  
}
