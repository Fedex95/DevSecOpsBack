package com.tienda.kpback.Controller;

import com.tienda.kpback.Entity.UsuarioEnt;
import com.tienda.kpback.Service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;  
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID; 

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;  
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    @Mock
    private UsuarioEnt mockUsuario;

    private UUID mockUserId;  

    @BeforeEach
    void setUp() {
        mockUserId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");  
    }

    @Test
    void testGetAllUsuarios() {
        List<UsuarioEnt> mockList = Arrays.asList(mockUsuario);
        when(usuarioService.getAllUsuarios()).thenReturn(mockList);

        ResponseEntity<List<UsuarioEnt>> response = usuarioController.getAllUsuarios();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockList, response.getBody());
    }

    @Test
    void testGetUsuarioById_Found() {
        Optional<UsuarioEnt> mockOptional = Optional.of(mockUsuario);
        when(usuarioService.getUsuarioById(any(UUID.class))).thenReturn(mockOptional); 

        ResponseEntity<UsuarioEnt> response = usuarioController.getUsuarioById(mockUserId);  
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockUsuario, response.getBody());
    }

    @Test
    void testGetUsuarioById_NotFound() {
        when(usuarioService.getUsuarioById(any(UUID.class))).thenReturn(Optional.empty()); 

        ResponseEntity<UsuarioEnt> response = usuarioController.getUsuarioById(mockUserId);  
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testCreateUsuario() {
        when(usuarioService.saveUsuario(any(UsuarioEnt.class))).thenReturn(mockUsuario);

        ResponseEntity<UsuarioEnt> response = usuarioController.createUsuario(mockUsuario);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockUsuario, response.getBody());
    }

    @Test
    void testEditUsuario_Success() {
        when(usuarioService.updateUsuario(any(UUID.class), any(UsuarioEnt.class))).thenReturn(mockUsuario); 
        ResponseEntity<UsuarioEnt> response = usuarioController.editUsuario(mockUserId, mockUsuario); 
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockUsuario, response.getBody());
    }

    @Test
    void testEditUsuario_NotFound() {
        when(usuarioService.updateUsuario(any(UUID.class), any(UsuarioEnt.class))).thenThrow(new RuntimeException("Not found")); 

        ResponseEntity<UsuarioEnt> response = usuarioController.editUsuario(mockUserId, mockUsuario);  
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteUsuario_Success() {
        doNothing().when(usuarioService).deleteUsuario(any(UUID.class)); 

        ResponseEntity<UsuarioEnt> response = usuarioController.deleteUsuario(mockUserId);  
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeleteUsuario_NotFound() {
        doThrow(new RuntimeException("Not found")).when(usuarioService).deleteUsuario(any(UUID.class)); 

        ResponseEntity<UsuarioEnt> response = usuarioController.deleteUsuario(mockUserId);  
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testViewPass_Correct() throws NoSuchAlgorithmException {
        Optional<UsuarioEnt> mockOptional = Optional.of(mockUsuario);
        when(usuarioService.getUsuarioByEmail(anyString())).thenReturn(mockOptional);  
        when(mockUsuario.getPass()).thenReturn("hashed");
        doReturn(true).when(usuarioService).checkPass(anyString(), anyString());

        ResponseEntity<String> response = usuarioController.viewPass("email@example.com", "pass"); 
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Correct Password", response.getBody());
    }

    @Test
    void testViewPass_Wrong() throws NoSuchAlgorithmException {
        Optional<UsuarioEnt> mockOptional = Optional.of(mockUsuario);
        when(usuarioService.getUsuarioByEmail(anyString())).thenReturn(mockOptional); 
        when(mockUsuario.getPass()).thenReturn("hashed");
        doReturn(false).when(usuarioService).checkPass(anyString(), anyString());

        ResponseEntity<String> response = usuarioController.viewPass("email@example.com", "pass");  
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Wrong Password", response.getBody());
    }

    @Test
    void testViewPass_UserNotFound() {
        when(usuarioService.getUsuarioByEmail(anyString())).thenReturn(Optional.empty());  

        ResponseEntity<String> response = usuarioController.viewPass("email@example.com", "pass");  
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Usuario no existente", response.getBody());
    }

    @Test
    void testViewPass_Error() {
        Optional<UsuarioEnt> mockOptional = Optional.of(mockUsuario);
        when(usuarioService.getUsuarioByEmail(anyString())).thenReturn(mockOptional);
        when(mockUsuario.getPass()).thenReturn("hashed");
        doThrow(new RuntimeException("Error")).when(usuarioService).checkPass(anyString(), anyString());

        assertThrows(RuntimeException.class, () -> {
            usuarioController.viewPass("email@example.com", "pass");
        });
    }

    @Test
    void testAdmin() {
        when(usuarioService.Admin(any(UUID.class))).thenReturn(true);  

        ResponseEntity<Boolean> response = usuarioController.Admin(mockUserId); 
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody());
    }
}