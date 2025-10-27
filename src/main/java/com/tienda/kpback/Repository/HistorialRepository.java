package com.tienda.kpback.Repository;

import com.tienda.kpback.Entity.Historial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialRepository extends JpaRepository<Historial, Long> {
    List<Historial> findByUsuarioId(Long usuarioId);
}
