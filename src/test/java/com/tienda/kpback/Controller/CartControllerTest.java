package com.tienda.kpback.Controller;

import com.tienda.kpback.Entity.Cart;
import com.tienda.kpback.Service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.tienda.kpback.Config.CustomUserDetails;

import java.util.HashMap;
import java.util.Map;
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
    private CustomUserDetails userDetails;

    private UUID mockUserId; 
    private UUID mockLibroId; 
    private UUID mockCartItemId; 

    @BeforeEach
    void setUp() {
        mockUserId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");  
        mockLibroId = UUID.fromString("456e7890-e89b-12d3-a456-426614174001");  
        mockCartItemId = UUID.fromString("789e0123-e89b-12d3-a456-426614174002"); 
    }

    @Test
    void testGetCart() {
        when(userDetails.getUserId()).thenReturn(mockUserId);  
        Cart mockCart = new Cart();
        when(cartService.getCartByUsuarioId(mockUserId)).thenReturn(mockCart);  

        ResponseEntity<Cart> response = cartController.getCart(userDetails);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockCart, response.getBody());
        verify(cartService).getCartByUsuarioId(mockUserId); 
    }

    @Test
    void testAddItemToCart() {
        when(userDetails.getUserId()).thenReturn(mockUserId); 
        Cart mockCart = new Cart();
        when(cartService.addItemToCart(mockUserId, mockLibroId, 3)).thenReturn(mockCart);  

        Map<String, Object> request = new HashMap<>();
        request.put("libroId", mockLibroId.toString()); 
        request.put("cantidad", 3);

        ResponseEntity<Cart> response = cartController.addItemToCart(userDetails, request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockCart, response.getBody());
        verify(cartService).addItemToCart(mockUserId, mockLibroId, 3); 
    }

    @Test
    void testUpdateItemCantidad() {
        when(userDetails.getUserId()).thenReturn(mockUserId);  
        Cart mockCart = new Cart();
        when(cartService.updateItemCart(mockCartItemId, 5, mockUserId)).thenReturn(mockCart); 

        ResponseEntity<Cart> response = cartController.updateItemCantidad(userDetails, mockCartItemId, 5);  
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockCart, response.getBody());
        verify(cartService).updateItemCart(mockCartItemId, 5, mockUserId);  
    }

    @Test
    void testDeleteItemCart() {
        when(userDetails.getUserId()).thenReturn(mockUserId); 
        doNothing().when(cartService).deleteItemCart(mockUserId, mockCartItemId);  

        ResponseEntity<Void> response = cartController.deleteItemCart(userDetails, mockCartItemId); 
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(cartService).deleteItemCart(mockUserId, mockCartItemId);
    }
}
