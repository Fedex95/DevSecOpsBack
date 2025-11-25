package com.tienda.kpback.Controller;

import com.tienda.kpback.Entity.Cart;
import com.tienda.kpback.Service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.tienda.kpback.Entity.UsuarioEnt;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @GetMapping("/get")
    public ResponseEntity<Cart> getCart( UsuarioEnt userDetails) {
        UUID userId = userDetails.getId();
        Cart cart = cartService.getCartByUsuarioId(userId);
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    @PostMapping("/agregar")
    public ResponseEntity<Cart> addItemToCart( UsuarioEnt userDetails, @RequestBody Map<String, Object> request) {
        UUID userId = userDetails.getId();
        UUID libroId = UUID.fromString((String) request.get("libroId"));  
        int cantidad = (int) request.get("cantidad");
        Cart updatedCart = cartService.addItemToCart(userId, libroId, cantidad);
        return new ResponseEntity<>(updatedCart, HttpStatus.OK);
    }

    @PostMapping("/actualizar/{cartItemId}")
    public ResponseEntity<Cart> updateItemCantidad( UsuarioEnt userDetails, @PathVariable UUID cartItemId, @RequestBody int cantidad) {
        UUID userId = userDetails.getId();
        Cart cart = cartService.updateItemCart(cartItemId, cantidad, userId);
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    @DeleteMapping("/eliminar/{itemId}")
    public ResponseEntity<Void> deleteItemCart( UsuarioEnt userDetails, @PathVariable UUID itemId) {
        UUID userId = userDetails.getId();
        cartService.deleteItemCart(userId, itemId);
        return ResponseEntity.noContent().build();
    }
}
