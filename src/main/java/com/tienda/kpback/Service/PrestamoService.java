package com.tienda.kpback.Service;

import com.tienda.kpback.Entity.*;
import com.tienda.kpback.Repository.PrestamoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;  

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

@Service
public class PrestamoService {  
    @Autowired
    private PrestamoRepository prestamoRepository;  

    @Autowired
    private UsuarioService usuarioService;

    @Transactional
    public Prestamo addPrestamo(Cart cart) {
        Prestamo prestamo = new Prestamo();
        prestamo.setUsuario(cart.getUsuario());
        prestamo.setFechaSolicitud(java.time.LocalDateTime.now());
        prestamo.setEstado(Prestamo.Estado.pendiente);

        Prestamo savedPrestamo = prestamoRepository.save(prestamo);

        return savedPrestamo;
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

    public List<Prestamo> getHistorialUsuario(UUID userId) {
        return prestamoRepository.findByUsuarioId(userId); 
    }
}
