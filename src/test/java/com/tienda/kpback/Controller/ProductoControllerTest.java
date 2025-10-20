package com.tienda.kpback.Controller;

import com.tienda.kpback.Entity.Producto;
import com.tienda.kpback.Repository.ProductoRepository;
import com.tienda.kpback.Service.ProductoService;
import com.tienda.kpback.Service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {

    @Mock
    private ProductoService productoService;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoController productoController;

    private Producto mockProducto;

    @BeforeEach
    void setUp() {
        mockProducto = new Producto(); 
    }

    @Test
    void testFindAllProductos() {
        List<Producto> mockList = Arrays.asList(mockProducto);
        when(productoRepository.findAll()).thenReturn(mockList);

        ResponseEntity<List<Producto>> response = productoController.findAllProductos();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockList, response.getBody());
    }

    @Test
    void testGetProductoById() {
        Optional<Producto> mockOptional = Optional.of(mockProducto);
        when(productoService.getProductoById(anyLong())).thenReturn(mockOptional);

        Optional<Producto> response = productoController.getProductoById(1L);
        assertEquals(mockOptional, response);
    }

    @Test
    void testCreateProducto_Success() {
        when(usuarioService.Admin(anyLong())).thenReturn(true);
        when(productoService.saveProducto(any(Producto.class))).thenReturn(mockProducto);

        ResponseEntity<Producto> response = productoController.createProducto(1L, mockProducto);
        assertEquals(201, response.getStatusCodeValue());
        assertEquals(mockProducto, response.getBody());
    }

    @Test
    void testCreateProducto_Forbidden() {
        when(usuarioService.Admin(anyLong())).thenReturn(false);

        ResponseEntity<Producto> response = productoController.createProducto(1L, mockProducto);
        assertEquals(403, response.getStatusCodeValue());
    }

    @Test
    void testUpdateProducto_Success() {
        when(usuarioService.Admin(anyLong())).thenReturn(true);
        when(productoService.updateProducto(any(Producto.class))).thenReturn(mockProducto);

        ResponseEntity<Producto> response = productoController.updateProducto(1L, 1L, mockProducto);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockProducto, response.getBody());
    }

    @Test
    void testUpdateProducto_Forbidden() {
        when(usuarioService.Admin(anyLong())).thenReturn(false);

        ResponseEntity<Producto> response = productoController.updateProducto(1L, 1L, mockProducto);
        assertEquals(403, response.getStatusCodeValue());
    }

    @Test
    void testDeleteProducto_Success() {
        when(usuarioService.Admin(anyLong())).thenReturn(true);
        doNothing().when(productoService).deleteProducto(anyLong());

        ResponseEntity<Void> response = productoController.deleteProducto(1L, 1L);
        assertEquals(204, response.getStatusCodeValue());
    }

    @Test
    void testDeleteProducto_Forbidden() {
        when(usuarioService.Admin(anyLong())).thenReturn(false);

        ResponseEntity<Void> response = productoController.deleteProducto(1L, 1L);
        assertEquals(403, response.getStatusCodeValue());
    }

    @Test
    void testDeleteProducto_InternalServerError() {
        when(usuarioService.Admin(anyLong())).thenReturn(true);
        doThrow(new RuntimeException("Error")).when(productoService).deleteProducto(anyLong());

        ResponseEntity<Void> response = productoController.deleteProducto(1L, 1L);
        assertEquals(500, response.getStatusCodeValue());
    }
}
