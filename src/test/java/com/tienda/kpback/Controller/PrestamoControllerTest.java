package com.tienda.kpback.Controller;

import com.tienda.kpback.Entity.Prestamo;
import com.tienda.kpback.Service.PrestamoService;
import com.tienda.kpback.Service.CartService;
import com.tienda.kpback.Service.TicketService;
import com.tienda.kpback.Entity.Cart;
import com.tienda.kpback.Entity.Ticket;
import com.tienda.kpback.Repository.PrestamoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;
import com.tienda.kpback.Config.CustomUserDetails;
import com.tienda.kpback.Entity.UsuarioEnt;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpStatus;
import java.util.UUID;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import java.util.Optional;
import com.tienda.kpback.Entity.CartItem; 


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) 
public class PrestamoControllerTest {

    @Mock
    private PrestamoService prestamoService;

    @Mock
    private CartService cartService;

    @Mock
    private TicketService ticketService;

    @Mock
    private PrestamoRepository prestamoRepository;

    @InjectMocks
    private PrestamoController prestamoController;

    @Mock
    private CustomUserDetails userDetails;

    @Mock
    private Prestamo mockPrestamo;

    @Mock
    private UsuarioEnt mockUsuario;  

    private UUID mockUserId;
    private Cart mockCart;
    private Ticket mockTicket;

    @BeforeEach
    void setUp() {
        mockUserId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        mockCart = mock(Cart.class);
        mockTicket = new Ticket();
        when(mockPrestamo.getUsuario()).thenReturn(mockUsuario);
        when(mockUsuario.getId()).thenReturn(mockUserId);
    }


    @Test
    void testGetAllPrestamos_Success() {
        when(userDetails.getRol()).thenReturn(UsuarioEnt.Rol.ADMIN);
        List<Prestamo> mockList = Arrays.asList(mockPrestamo);
        when(prestamoService.findAll()).thenReturn(mockList);

        ResponseEntity<List<Prestamo>> response = prestamoController.getAllPrestamos(userDetails);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockList, response.getBody());
    }

    @Test
    void testGetAllPrestamos_Error() {
        when(userDetails.getRol()).thenReturn(UsuarioEnt.Rol.USER);
        ResponseEntity<List<Prestamo>> response = prestamoController.getAllPrestamos(userDetails);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testCreatePrestamo_Success() {
        when(userDetails.getUserId()).thenReturn(mockUserId);
        when(cartService.getCartByUsuarioId(mockUserId)).thenReturn(mockCart);
        when(mockCart.getItems()).thenReturn(Arrays.asList(mock(CartItem.class)));  
        when(prestamoService.addPrestamo(mockCart)).thenReturn(mockPrestamo);

        ResponseEntity<?> response = prestamoController.createPrestamo(userDetails);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockPrestamo, response.getBody());
        verify(ticketService).generateTicket(mockPrestamo);
        verify(cartService).clearCart(mockUserId);
    }

    @Test
    void testCreatePrestamo_CartEmpty() {
        when(userDetails.getUserId()).thenReturn(mockUserId);
        when(cartService.getCartByUsuarioId(mockUserId)).thenReturn(mockCart);
        when(mockCart.getItems()).thenReturn(Arrays.asList());  

        ResponseEntity<?> response = prestamoController.createPrestamo(userDetails);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("El carrito está vacío", response.getBody());
    }

    @Test
    void testCreatePrestamo_Error() {
        when(userDetails.getUserId()).thenReturn(mockUserId);
        when(cartService.getCartByUsuarioId(mockUserId)).thenThrow(new RuntimeException("Error"));

        ResponseEntity<?> response = prestamoController.createPrestamo(userDetails);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error", response.getBody());
    }

    @Test
    void testUpdateEstado_Success() {
        when(userDetails.getRol()).thenReturn(UsuarioEnt.Rol.ADMIN);
        Map<String, Object> request = Map.of("estado", "devuelto");

        ResponseEntity<?> response = prestamoController.updateEstado(mockUserId, request, userDetails);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(prestamoService).updateEstado(mockUserId, Prestamo.Estado.devuelto);
    }

    @Test
    void testUpdateEstado_Forbidden() {
        when(userDetails.getRol()).thenReturn(UsuarioEnt.Rol.USER);
        Map<String, Object> request = Map.of("estado", "devuelto");

        ResponseEntity<?> response = prestamoController.updateEstado(mockUserId, request, userDetails);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testUpdateEstado_Error() {
        when(userDetails.getRol()).thenReturn(UsuarioEnt.Rol.ADMIN);
        Map<String, Object> request = Map.of("estado", "devuelto");
        doThrow(new RuntimeException()).when(prestamoService).updateEstado(mockUserId, Prestamo.Estado.devuelto);

        ResponseEntity<?> response = prestamoController.updateEstado(mockUserId, request, userDetails);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetTicket_Success() {
        when(prestamoRepository.findById(mockUserId)).thenReturn(Optional.of(mockPrestamo));
        when(userDetails.getUserId()).thenReturn(mockUserId);
        when(prestamoService.findTicketByPrestamoId(mockUserId)).thenReturn(Optional.of(mockTicket));

        ResponseEntity<Ticket> response = prestamoController.getTicket(mockUserId, userDetails);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockTicket, response.getBody());
    }

    @Test
    void testGetTicket_NotFound() {
        when(prestamoRepository.findById(mockUserId)).thenReturn(Optional.empty());

        ResponseEntity<Ticket> response = prestamoController.getTicket(mockUserId, userDetails);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetTicket_Forbidden() {
        when(prestamoRepository.findById(mockUserId)).thenReturn(Optional.of(mockPrestamo));
        when(userDetails.getUserId()).thenReturn(UUID.randomUUID());  
        when(userDetails.getRol()).thenReturn(UsuarioEnt.Rol.USER);

        ResponseEntity<Ticket> response = prestamoController.getTicket(mockUserId, userDetails);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}