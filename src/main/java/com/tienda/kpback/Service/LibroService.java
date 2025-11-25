package com.tienda.kpback.Service;

import com.tienda.kpback.Entity.Libro;
import com.tienda.kpback.Repository.LibroRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LibroService {
    @Autowired
    private LibroRepository libroRepository;

    public List<Libro> getAllLibros() {
        return libroRepository.findAll();
    }

    public Optional<Libro> getLibroById(UUID libroId) {
        Optional<Libro> libro = libroRepository.findById(libroId);
        if (libro.isEmpty()) {
            System.out.println("No se encontró el libro con ID: " + libroId);
        }
        return libro;
    }

    public Libro saveLibro(Libro libro) {
        return libroRepository.save(libro);
    }

    public boolean deleteLibro(UUID id) {
        if (libroRepository.existsById(id)) {
            libroRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public Optional<Libro> updateLibro(UUID id, Libro libroDetails) {
        Optional<Libro> existing = libroRepository.findById(id);
        if (existing.isPresent()) {
            Libro l = existing.get();
            l.setTitulo(libroDetails.getTitulo());
            l.setAutor(libroDetails.getAutor());
            l.setCategoria(libroDetails.getCategoria());
            l.setDescripcion(libroDetails.getDescripcion());
            l.setIsbn(libroDetails.getIsbn());
            l.setImagenUrl(libroDetails.getImagenUrl());
            l.setCopiasDisponibles(libroDetails.getCopiasDisponibles());
            return Optional.of(libroRepository.save(l));
        }
        return Optional.empty();
    }

    @Transactional
    public boolean updateCopiasDisponibles(UUID id, int copiasDisponibles) {
        Optional<Libro> existing = libroRepository.findById(id);
        if (existing.isPresent()) {
            Libro l = existing.get();
            l.setCopiasDisponibles(copiasDisponibles);
            libroRepository.save(l);
            return true;
        }
        return false;
    }
}
