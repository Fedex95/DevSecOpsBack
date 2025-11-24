package com.tienda.kpback.Controller;

import com.tienda.kpback.Entity.Libro;
import com.tienda.kpback.Service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.Map;

import java.util.List;
import java.util.Optional;
import com.tienda.kpback.Config.CustomUserDetails;
import com.tienda.kpback.Entity.UsuarioEnt;
import java.util.UUID;  

@RestController
@RequestMapping("/api/libros")
@SecurityRequirement(name = "bearerAuth") 
public class LibroController {
    @Autowired
    private LibroService libroService;

    @GetMapping 
    public ResponseEntity<List<Libro>> getAllLibros() {
        List<Libro> libros = libroService.getAllLibros();
        return ResponseEntity.ok(libros);
    }

    @GetMapping("/{id}") 
    public ResponseEntity<Libro> getLibroById(@PathVariable UUID id) { 
        Optional<Libro> libro = libroService.getLibroById(id);
        return libro.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Libro> createLibro(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody Libro libro) {
        if (userDetails.getRol() != UsuarioEnt.Rol.ADMIN) {
            return ResponseEntity.status(403).build();
        }
        Libro savedLibro = libroService.saveLibro(libro);
        return ResponseEntity.ok(savedLibro);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Libro> updateLibro(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable UUID id, @RequestBody Libro libroDetails) {  
        if (userDetails.getRol() != UsuarioEnt.Rol.ADMIN) {
            return ResponseEntity.status(403).build();
        }
        Optional<Libro> updatedLibro = libroService.updateLibro(id, libroDetails);
        return updatedLibro.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLibro(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable UUID id) {  
        if (userDetails.getRol() != UsuarioEnt.Rol.ADMIN) {
            return ResponseEntity.status(403).build();
        }
        if (libroService.deleteLibro(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/copias")
    public ResponseEntity<Void> updateCopiasDisponibles(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable UUID id, @io.swagger.v3.oas.annotations.parameters.RequestBody(  
        description = "Número de copias disponibles",
        required = true,
        content = @io.swagger.v3.oas.annotations.media.Content(
            mediaType = "application/json",
            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                value = "{\"copiasDisponibles\": 10}"
            )
        )
    ) @RequestBody Map<String, Integer> request) {
        if (userDetails.getRol() != UsuarioEnt.Rol.ADMIN) {
            return ResponseEntity.status(403).build();
        }
        Integer copias = request.get("copiasDisponibles");
        if (copias == null) {
            return ResponseEntity.badRequest().build();
        }
        if (libroService.updateCopiasDisponibles(id, copias)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
