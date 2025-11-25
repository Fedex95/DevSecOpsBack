package com.tienda.kpback.Controller;

import com.tienda.kpback.Entity.Prestamo;
import com.tienda.kpback.Service.PrestamoService;
import com.tienda.kpback.Service.CartService;  
import com.tienda.kpback.Service.TicketService;  
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.tienda.kpback.Config.CustomUserDetails;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;
import com.tienda.kpback.Entity.UsuarioEnt;
import com.tienda.kpback.Entity.Ticket;
import com.tienda.kpback.Repository.PrestamoRepository;

@RestController
@RequestMapping("/api/prestamos")
@SecurityRequirement(name = "bearerAuth")

public class PrestamoController {
    @Autowired
    private PrestamoService prestamoService;

    @Autowired
    private CartService cartService;  

    @Autowired
    private TicketService ticketService; 

    @Autowired
    private PrestamoRepository prestamoRepository;

    @GetMapping("/historial")
    @Transactional(readOnly = true)  
    public ResponseEntity<List<Prestamo>> getHistorialUsuario(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails.getRol() != UsuarioEnt.Rol.USER && userDetails.getRol() != UsuarioEnt.Rol.ADMIN) {  
            return ResponseEntity.status(403).build();
        }
        UUID userId = userDetails.getUserId();
        List<Prestamo> prestamos = prestamoService.findByUsuarioId(userId); 
        return ResponseEntity.ok(prestamos);
    }

    @GetMapping("/all")
    @Transactional(readOnly = true)  
    public ResponseEntity<List<Prestamo>> getAllPrestamos(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails.getRol() == UsuarioEnt.Rol.ADMIN) {  
            List<Prestamo> prestamos = prestamoService.findAll();
            return ResponseEntity.ok(prestamos);
        } else {
            return ResponseEntity.status(403).build();
        }
    }

    @PostMapping  
    public ResponseEntity<?> createPrestamo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID userId = userDetails.getUserId();
        try {
            var cart = cartService.getCartByUsuarioId(userId);
            if (cart.getItems().isEmpty()) {
                return ResponseEntity.badRequest().body("El carrito está vacío");  
            }

            Prestamo prestamo = prestamoService.addPrestamo(cart);

            ticketService.generateTicket(prestamo);

            cartService.clearCart(userId);

            return ResponseEntity.ok(prestamo);
        } catch (RuntimeException e) {
            
            return ResponseEntity.badRequest().body(e.getMessage());  // 400 con mensaje
        } catch (Exception e) {
            // Otros errores
            return ResponseEntity.status(500).body("Error interno del servidor");  // 500 genérico
        }
    }

    @PutMapping("/{id}/estado") 
    @Transactional  
    public ResponseEntity<?> updateEstado(@PathVariable UUID id, @RequestBody Map<String, Object> request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails.getRol() != UsuarioEnt.Rol.ADMIN) {  
            return ResponseEntity.status(403).build();
        }
        try {
            String estadoStr = (String) request.get("estado");
            Prestamo.Estado estado = Prestamo.Estado.valueOf(estadoStr);  
            prestamoService.updateEstado(id, estado);
            return ResponseEntity.noContent().build();  // 204: evita serializar entidad con proxies
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/ticket")
    @Transactional(readOnly = true)
    public ResponseEntity<Ticket> getTicket(@PathVariable UUID id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Optional<Prestamo> prestamoOpt = prestamoRepository.findById(id);
        if (prestamoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Prestamo prestamo = prestamoOpt.get();
        // Verificar permisos: propietario o admin
        if (!prestamo.getUsuario().getId().equals(userDetails.getUserId()) && userDetails.getRol() != UsuarioEnt.Rol.ADMIN) {
            return ResponseEntity.status(403).build();
        }
        Optional<Ticket> ticket = prestamoService.findTicketByPrestamoId(id);
        return ticket.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
