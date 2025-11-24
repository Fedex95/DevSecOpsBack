package com.tienda.kpback.Service;

import com.tienda.kpback.Entity.*;
import com.tienda.kpback.Repository.PrestamoRepository;
import com.tienda.kpback.Repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;  

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PrestamoService {  
    @Autowired
    private PrestamoRepository prestamoRepository;  

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private LibroRepository libroRepository; 

    @Transactional
    public Prestamo addPrestamo(Cart cart) {
        Prestamo prestamo = new Prestamo();
        prestamo.setUsuario(cart.getUsuario());
        prestamo.setFechaSolicitud(java.time.LocalDateTime.now());
        prestamo.setEstado(Prestamo.Estado.pendiente);

        List<DetallePrestamo> detalles = cart.getItems().stream().map(item -> { 
            Libro libro = item.getLibro();
            
            // Verificar y restar copias
            if (libro.getCopiasDisponibles() < item.getCantidad()) {
                throw new RuntimeException("No hay suficientes copias disponibles para " + libro.getTitulo());
            }
            libro.setCopiasDisponibles(libro.getCopiasDisponibles() - item.getCantidad());
            libroRepository.save(libro);  // Guardar la actualización
            
            DetallePrestamo detalle = new DetallePrestamo();
            detalle.setPrestamo(prestamo);
            detalle.setLibro(libro);  
            detalle.setCantidad(item.getCantidad());
            return detalle;
        }).collect(Collectors.toList());

        prestamo.setDetallesPrestamo(detalles);

        Prestamo savedPrestamo = prestamoRepository.save(prestamo);

        return savedPrestamo;
    }

    @Transactional(readOnly = true)  
    public List<Prestamo> findByUsuarioId(UUID userId) {
        return prestamoRepository.findByUsuarioIdWithUsuario(userId);
    }

    @Transactional(readOnly = true)  
    public List<Prestamo> findByUsuarioIdAndEstado(UUID userId, Prestamo.Estado estado) {
        return prestamoRepository.findByUsuarioIdAndEstadoWithUsuario(userId, estado);
    }

    @Transactional(readOnly = true) 
    public List<Prestamo> findAll() {
        return prestamoRepository.findAllWithUsuario(); 
    }

    @Transactional
    public Prestamo updateEstado(UUID id, Prestamo.Estado estado) {  
        Prestamo prestamo = prestamoRepository.findById(id)  
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));
        prestamo.setEstado(estado);
        if (estado == Prestamo.Estado.retirado) {
            prestamo.setFechaRetiro(LocalDateTime.now());
        } else if (estado == Prestamo.Estado.devuelto) {
            prestamo.setFechaDevolucion(LocalDateTime.now());
        }
        return prestamoRepository.save(prestamo);
    }

    public List<Prestamo> getAllPrestamosAdmin(UUID userId) {
        if (usuarioService.Admin(userId)) {
            return prestamoRepository.findAll();
        } else {
            throw new RuntimeException("Acceso denegado");
        }
    }

    @Transactional(readOnly = true)
    public Optional<Ticket> findTicketByPrestamoId(UUID id) {
        return prestamoRepository.findById(id).map(Prestamo::getTicket);
    }

    public List<Prestamo> getHistorialUsuario(UUID userId) {
        return prestamoRepository.findByUsuarioId(userId); 
    }
}
