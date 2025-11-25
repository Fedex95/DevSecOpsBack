package com.tienda.kpback.Repository;

import com.tienda.kpback.Entity.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;  
import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro, UUID> {  
    List<Libro> findByAutor(String autor);  
    Optional<Libro> findByIsbn(String isbn);  
}
