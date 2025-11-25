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
    void testCreateUsuario_Success_Params() {
        // saveUsuario returns entity on success
        when(usuarioService.saveUsuario(any(UsuarioEnt.class))).thenReturn(mockUsuario);

        ResponseEntity<String> response = usuarioController.createUsuario("John", "Doe", "email@example.com", "password123");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Usuario created", response.getBody());
    }

    @Test
    void testCreateUsuario_InvalidInput() {
        when(usuarioService.saveUsuario(any(UsuarioEnt.class))).thenThrow(new IllegalArgumentException("Invalid input"));

        ResponseEntity<String> response = usuarioController.createUsuario("John", "Doe", "invalid-email", "pass");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid input", response.getBody());
    }

    @Test
    void testCreateUsuario_Error() {
        when(usuarioService.saveUsuario(any(UsuarioEnt.class))).thenThrow(new RuntimeException("Error"));

        ResponseEntity<String> response = usuarioController.createUsuario("John", "Doe", "email@example.com", "password123");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error creating user", response.getBody());
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
        when(usuarioService.getUsuarioByEmailAndPass(anyString(), anyString())).thenReturn(mockOptional);

        ResponseEntity<String> response = usuarioController.viewPass("email@example.com", "password123");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Correct Password", response.getBody());
    }

    @Test
    void testViewPass_Wrong() throws NoSuchAlgorithmException {
        when(usuarioService.getUsuarioByEmailAndPass(anyString(), anyString())).thenReturn(Optional.empty());

        ResponseEntity<String> response = usuarioController.viewPass("email@example.com", "password123");
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody());
    }

    @Test
    void testViewPass_UserNotFound() {
        when(usuarioService.getUsuarioByEmailAndPass(anyString(), anyString())).thenReturn(Optional.empty());

        ResponseEntity<String> response = usuarioController.viewPass("email@example.com", "password123");
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody());
    }

    @Test
    void testViewPass_InvalidEmail() {
        ResponseEntity<String> response = usuarioController.viewPass("invalid-email", "password123");
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody());
    }

    @Test
    void testViewPass_InvalidPass() {
        ResponseEntity<String> response = usuarioController.viewPass("email@example.com", "short");
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody());
    }

    @Test
    void testViewPass_SQLInjection_DenyList() {
        ResponseEntity<String> response = usuarioController.viewPass("email@example.com", "case randomblob(100000) when not null then 1 else 1 end");
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody());
    }

    @Test
    void testViewPass_Error() {
        when(usuarioService.getUsuarioByEmailAndPass(anyString(), anyString())).thenThrow(new RuntimeException("Error"));

        assertThrows(RuntimeException.class, () -> {
            usuarioController.viewPass("email@example.com", "password123");
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