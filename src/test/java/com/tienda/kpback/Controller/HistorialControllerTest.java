package com.tienda.kpback.Controller;

import com.tienda.kpback.Entity.Historial;
import com.tienda.kpback.Service.HistorialService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HistorialControllerTest {

    @Mock
    private HistorialService historialService;

    @InjectMocks
    private HistorialController historialController;

    private Historial mockHistorial;

    @BeforeEach
    void setUp() {
        mockHistorial = new Historial();  
    }

    @Test
    void testGetHistorial() {
        List<Historial> mockList = Arrays.asList(mockHistorial);
        when(historialService.findByUsuarioId(anyLong())).thenReturn(mockList);

        ResponseEntity<List<Historial>> response = historialController.getHistorial(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockList, response.getBody());
    }

    @Test
    void testGetAllHistorials_Success() {
        List<Historial> mockList = Arrays.asList(mockHistorial);
        when(historialService.getHistorialAdmin(anyLong())).thenReturn(mockList);

        ResponseEntity<List<Historial>> response = historialController.getAllHistorials(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockList, response.getBody());
    }

    @Test
    void testGetAllHistorials_Error() {
        when(historialService.getHistorialAdmin(anyLong())).thenThrow(new RuntimeException("Access denied"));

        ResponseEntity<List<Historial>> response = historialController.getAllHistorials(1L);
        assertEquals(403, response.getStatusCodeValue());
    }
}
