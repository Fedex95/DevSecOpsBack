package com.tienda.kpback.Controller;

import com.tienda.kpback.Entity.UsuarioEnt;
import com.tienda.kpback.Service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
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

    @Test
    void testGetAllUsuarios() {
        List<UsuarioEnt> mockList = Arrays.asList(mockUsuario);
        when(usuarioService.getAllUsuarios()).thenReturn(mockList);

        ResponseEntity<List<UsuarioEnt>> response = usuarioController.getAllUsuarios();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockList, response.getBody());
    }

    @Test
    void testGetUsuarioById_Found() {
        Optional<UsuarioEnt> mockOptional = Optional.of(mockUsuario);
        when(usuarioService.getUsuarioById(anyLong())).thenReturn(mockOptional);

        ResponseEntity<UsuarioEnt> response = usuarioController.getUsuarioById(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockUsuario, response.getBody());
    }

    @Test
    void testGetUsuarioById_NotFound() {
        when(usuarioService.getUsuarioById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<UsuarioEnt> response = usuarioController.getUsuarioById(1L);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testCreateUsuario() {
        when(usuarioService.saveUsuario(any(UsuarioEnt.class))).thenReturn(mockUsuario);

        ResponseEntity<UsuarioEnt> response = usuarioController.createUsuario(mockUsuario);
        assertEquals(201, response.getStatusCodeValue());
        assertEquals(mockUsuario, response.getBody());
    }

    @Test
    void testEditUsuario_Success() {
        when(usuarioService.updateUsuario(anyLong(), any(UsuarioEnt.class))).thenReturn(mockUsuario);

        ResponseEntity<UsuarioEnt> response = usuarioController.editUsuario(1L, mockUsuario);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockUsuario, response.getBody());
    }

    @Test
    void testEditUsuario_NotFound() {
        when(usuarioService.updateUsuario(anyLong(), any(UsuarioEnt.class))).thenThrow(new RuntimeException("Not found"));

        ResponseEntity<UsuarioEnt> response = usuarioController.editUsuario(1L, mockUsuario);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testDeleteUsuario_Success() {
        doNothing().when(usuarioService).deleteUsuario(anyLong());

        ResponseEntity<UsuarioEnt> response = usuarioController.deleteUsuario(1L);
        assertEquals(204, response.getStatusCodeValue());
    }

    @Test
    void testDeleteUsuario_NotFound() {
        doThrow(new RuntimeException("Not found")).when(usuarioService).deleteUsuario(anyLong());

        ResponseEntity<UsuarioEnt> response = usuarioController.deleteUsuario(1L);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testViewPass_Correct() throws NoSuchAlgorithmException {
        Optional<UsuarioEnt> mockOptional = Optional.of(mockUsuario);
        when(usuarioService.getUsuarioByUsuario(anyString())).thenReturn(mockOptional);
        when(mockUsuario.getPass()).thenReturn("hashed");   
        doReturn(true).when(usuarioService).checkPass(anyString(), anyString());

        ResponseEntity<String> response = usuarioController.viewPass("user", "pass");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Correct Password", response.getBody());
    }

    @Test
    void testViewPass_Wrong() throws NoSuchAlgorithmException {
        Optional<UsuarioEnt> mockOptional = Optional.of(mockUsuario);
        when(usuarioService.getUsuarioByUsuario(anyString())).thenReturn(mockOptional);
        when(mockUsuario.getPass()).thenReturn("hashed");  
        doReturn(false).when(usuarioService).checkPass(anyString(), anyString());

        ResponseEntity<String> response = usuarioController.viewPass("user", "pass");
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Wrong Password", response.getBody());
    }

    @Test
    void testViewPass_UserNotFound() {
        when(usuarioService.getUsuarioByUsuario(anyString())).thenReturn(Optional.empty());

        ResponseEntity<String> response = usuarioController.viewPass("user", "pass");
        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Usuario no existente", response.getBody());
    }

    @Test
    void testViewPass_Error() throws NoSuchAlgorithmException {
        Optional<UsuarioEnt> mockOptional = Optional.of(mockUsuario);
        when(usuarioService.getUsuarioByUsuario(anyString())).thenReturn(mockOptional);
        when(mockUsuario.getPass()).thenReturn("hashed");  
        doThrow(new NoSuchAlgorithmException()).when(usuarioService).checkPass(anyString(), anyString());

        ResponseEntity<String> response = usuarioController.viewPass("user", "pass");
        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Error", response.getBody());
    }

    @Test
    void testAdmin() {
        when(usuarioService.Admin(anyLong())).thenReturn(true);

        ResponseEntity<Boolean> response = usuarioController.Admin(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(true, response.getBody());
    }
}