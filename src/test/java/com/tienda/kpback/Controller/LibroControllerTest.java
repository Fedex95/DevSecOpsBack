package com.tienda.kpback.Controller;

import com.tienda.kpback.Config.CustomUserDetails;
import com.tienda.kpback.Entity.Libro; 
import com.tienda.kpback.Entity.UsuarioEnt;
import com.tienda.kpback.Service.LibroService;  
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.tienda.kpback.Repository.LibroRepository;  
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;  
import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibroControllerTest { 

    @Mock
    private LibroService libroService;  
    @Mock
    private LibroRepository libroRepository;  

    @InjectMocks
    private LibroController libroController;  

    @Mock
    private CustomUserDetails userDetails;

    private Libro mockLibro;
    private UUID mockId; 

    @BeforeEach
    void setUp() {
        mockId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");  
        mockLibro = new Libro();  
        mockLibro.setId(mockId); 
        mockLibro.setTitulo("Libro Test");  
    }

    @Test
    void testGetAllLibros() {  
        List<Libro> mockList = Arrays.asList(mockLibro);  
        when(libroService.getAllLibros()).thenReturn(mockList);  

        ResponseEntity<List<Libro>> response = libroController.getAllLibros();  
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockList, response.getBody());
    }

    @Test
    void testGetLibroById() {  
        Optional<Libro> mockOptional = Optional.of(mockLibro);  
        when(libroService.getLibroById(mockId)).thenReturn(mockOptional); 

        ResponseEntity<Libro> response = libroController.getLibroById(mockId); 
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockLibro, response.getBody());
    }

    @Test
    void testCreateLibro_Success() { 
        when(userDetails.getRol()).thenReturn(UsuarioEnt.Rol.ADMIN);
        when(libroService.saveLibro(any(Libro.class))).thenReturn(mockLibro); 

        ResponseEntity<Libro> response = libroController.createLibro(userDetails, mockLibro);  
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockLibro, response.getBody());
    }

    @Test
    void testCreateLibro_Forbidden() {  
        when(userDetails.getRol()).thenReturn(UsuarioEnt.Rol.USER);

        ResponseEntity<Libro> response = libroController.createLibro(userDetails, mockLibro); 
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testUpdateLibro_Success() {  
        when(userDetails.getRol()).thenReturn(UsuarioEnt.Rol.ADMIN);
        Optional<Libro> mockOptional = Optional.of(mockLibro);  
        when(libroService.updateLibro(eq(mockId), any(Libro.class))).thenReturn(mockOptional);  

        ResponseEntity<Libro> response = libroController.updateLibro(userDetails, mockId, mockLibro);  
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockLibro, response.getBody());
    }

    @Test
    void testUpdateLibro_Forbidden() {
        when(userDetails.getRol()).thenReturn(UsuarioEnt.Rol.USER);

        ResponseEntity<Libro> response = libroController.updateLibro(userDetails, mockId, mockLibro); 
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testDeleteLibro_Success() {  
        when(userDetails.getRol()).thenReturn(UsuarioEnt.Rol.ADMIN);
        when(libroService.deleteLibro(mockId)).thenReturn(true); 

        ResponseEntity<Void> response = libroController.deleteLibro(userDetails, mockId);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeleteLibro_Forbidden() {
        when(userDetails.getRol()).thenReturn(UsuarioEnt.Rol.USER);

        ResponseEntity<Void> response = libroController.deleteLibro(userDetails, mockId); 
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testUpdateCopiasDisponibles_Success() {
        when(userDetails.getRol()).thenReturn(UsuarioEnt.Rol.ADMIN);
        when(libroService.updateCopiasDisponibles(eq(mockId), eq(10))).thenReturn(true);

        Map<String, Integer> request = new HashMap<>();
        request.put("copiasDisponibles", 10);

        ResponseEntity<Void> response = libroController.updateCopiasDisponibles(userDetails, mockId, request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUpdateCopiasDisponibles_Forbidden() {
        when(userDetails.getRol()).thenReturn(UsuarioEnt.Rol.USER);

        Map<String, Integer> request = new HashMap<>();
        request.put("copiasDisponibles", 10);

        ResponseEntity<Void> response = libroController.updateCopiasDisponibles(userDetails, mockId, request);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testUpdateCopiasDisponibles_BadRequest() {
        when(userDetails.getRol()).thenReturn(UsuarioEnt.Rol.ADMIN);

        Map<String, Integer> request = new HashMap<>();  

        ResponseEntity<Void> response = libroController.updateCopiasDisponibles(userDetails, mockId, request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testUpdateCopiasDisponibles_NotFound() {
        when(userDetails.getRol()).thenReturn(UsuarioEnt.Rol.ADMIN);
        when(libroService.updateCopiasDisponibles(eq(mockId), eq(10))).thenReturn(false);

        Map<String, Integer> request = new HashMap<>();
        request.put("copiasDisponibles", 10);

        ResponseEntity<Void> response = libroController.updateCopiasDisponibles(userDetails, mockId, request);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
