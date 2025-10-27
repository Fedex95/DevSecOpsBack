package com.tienda.kpback.Controller;

import com.tienda.kpback.Entity.Cart;
import com.tienda.kpback.Service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testGetCartByUsuarioId() {
        Cart mockCart = new Cart();
        when(cartService.createCarrito(anyLong())).thenReturn(mockCart);

        ResponseEntity<Cart> response = cartController.getCartByUsuarioId(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockCart, response.getBody());
    }

    @Test
    void testAddItemToCart() {
        Cart mockCart = new Cart();
        when(cartService.addItemToCart(anyLong(), anyLong(), anyInt())).thenReturn(mockCart);

        Map<String, Object> request = new HashMap<>();
        request.put("usuarioId", 1L);
        request.put("productoId", 2L);
        request.put("cantidad", 3);

        ResponseEntity<Cart> response = cartController.addItemToCart(request);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockCart, response.getBody());
    }

    @Test
    void testUpdateItemCantidad() {
        Cart mockCart = new Cart();
        when(cartService.updateItemCart(anyLong(), anyInt())).thenReturn(mockCart);

        ResponseEntity<Cart> response = cartController.updateItemCantidad(1L, 5);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockCart, response.getBody());
    }

    @Test
    void testDeleteItemCart() {
        doNothing().when(cartService).deleteItemCart(anyLong(), anyLong());

        ResponseEntity<Void> response = cartController.deleteItemCart(1L, 2L);
        assertEquals(204, response.getStatusCodeValue());
    }

    @Test
    void testPayCart() {
        doNothing().when(cartService).Pago(anyLong());

        ResponseEntity<Map<String, String>> response = cartController.payCart(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("success", response.getBody().get("status"));
    }
}
