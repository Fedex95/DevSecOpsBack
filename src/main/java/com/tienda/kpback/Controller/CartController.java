package com.tienda.kpback.Controller;

import com.tienda.kpback.Entity.Cart;
import com.tienda.kpback.Service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @GetMapping("/{usuarioId}")
    public ResponseEntity<Cart> getCartByUsuarioId(@PathVariable Long usuarioId){
        Cart cart = cartService.createCarrito(usuarioId);
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    @PostMapping("/agregar")
    public ResponseEntity<Cart> addItemToCart(@RequestBody Map<String, Object> request){
        Long usuarioId = ((Number) request.get("usuarioId")).longValue();
        Long productoId = ((Number) request.get("productoId")).longValue();
        int cantidad = (int) request.get("cantidad");
        Cart updatedCart = cartService.addItemToCart(usuarioId, productoId, cantidad);
        return new ResponseEntity<>(updatedCart, HttpStatus.OK);
    }

    @PostMapping("/actualizar/{cartItemId}")
    public ResponseEntity<Cart> updateItemCantidad(@PathVariable Long cartItemId, @RequestBody int cantidad){
        Cart cart = cartService.updateItemCart(cartItemId, cantidad);
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    @DeleteMapping("/eliminar/{cartId}/{itemId}")
    public ResponseEntity<Void> deleteItemCart(@PathVariable Long cartId, @PathVariable Long itemId){
        cartService.deleteItemCart(cartId, itemId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/pagar/{cartId}")
    public ResponseEntity<Map<String, String>> payCart(@PathVariable Long cartId){
        cartService.Pago(cartId);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");

        return ResponseEntity.ok(response);
    }
}
