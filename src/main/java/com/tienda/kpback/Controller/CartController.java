package com.tienda.kpback.Controller;

import com.tienda.kpback.Entity.Cart;
import com.tienda.kpback.Service.CartService;
import com.tienda.kpback.Entity.UsuarioEnt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @GetMapping("/get")
    public ResponseEntity<Cart> getCart(UsuarioEnt usuario) {
        UUID userId = usuario.getId();
        Cart cart = cartService.getCartByUsuarioId(userId);
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    @DeleteMapping("/eliminar/{itemId}")
    public ResponseEntity<Void> deleteItemCart(UsuarioEnt usuario, @PathVariable UUID itemId) {
        UUID userId = usuario.getId();
        cartService.deleteItemCart(userId, itemId);
        return ResponseEntity.noContent().build();
    }
}
