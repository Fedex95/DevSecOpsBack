package com.tienda.kpback.Controller;

import com.tienda.kpback.Entity.Cart;
import com.tienda.kpback.Entity.UsuarioEnt;
import com.tienda.kpback.Service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import java.util.UUID;  

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    @Mock
    private UsuarioEnt usuario;

    private UUID mockUserId; 
    private UUID mockCartItemId; 

    @BeforeEach
    void setUp() {
        mockUserId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");  
        mockCartItemId = UUID.fromString("789e0123-e89b-12d3-a456-426614174002"); 
    }

    @Test
    void testGetCart() {
        when(usuario.getId()).thenReturn(mockUserId);  
        Cart mockCart = new Cart();
        when(cartService.getCartByUsuarioId(mockUserId)).thenReturn(mockCart);  

        ResponseEntity<Cart> response = cartController.getCart(usuario);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockCart, response.getBody());
        verify(cartService).getCartByUsuarioId(mockUserId); 
    }

    @Test
    void testDeleteItemCart() {
        when(usuario.getId()).thenReturn(mockUserId); 
        doNothing().when(cartService).deleteItemCart(mockUserId, mockCartItemId);  

        ResponseEntity<Void> response = cartController.deleteItemCart(usuario, mockCartItemId); 
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(cartService).deleteItemCart(mockUserId, mockCartItemId);
    }
}
