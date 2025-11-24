package com.tienda.kpback.Service;

import com.tienda.kpback.Entity.*;
import com.tienda.kpback.Repository.CartItemRepository;
import com.tienda.kpback.Repository.CartRepository;
import com.tienda.kpback.Repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;  

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private LibroService libroService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Cart getCartByUsuarioId(UUID usuarioId) { 
        UsuarioEnt usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return cartRepository.findByUsuario(usuario)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUsuario(usuario);
                    return cartRepository.save(newCart);
                });
    }

    public Cart addItemToCart(UUID usuarioId, UUID libroId, int cantidad) {  
        Cart cart = getCartByUsuarioId(usuarioId);
        Libro libro = libroService.getLibroById(libroId)  
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));

        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getLibro().getId().equals(libroId))  
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setCantidad(existingItem.getCantidad() + cantidad);
            cartItemRepository.save(existingItem);
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setLibro(libro);
            item.setCantidad(cantidad);
            cart.getItems().add(item);
            cartItemRepository.save(item);
        }

        return cartRepository.save(cart);
    }

    public Cart updateItemCart(UUID cartItemId, int cantidad, UUID usuarioId) {  
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));
        if (!item.getCart().getUsuario().getId().equals(usuarioId)) { 
            throw new RuntimeException("No autorizado para actualizar este item");
        }
        item.setCantidad(cantidad);
        cartItemRepository.save(item);
        return item.getCart();
    }

    public void deleteItemCart(UUID usuarioId, UUID itemId) {  
        UsuarioEnt usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Cart cart = cartRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        CartItem itemDelete = cart.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item no encontrado en el carrito"));
        cart.getItems().remove(itemDelete);
        cartItemRepository.delete(itemDelete);
        cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(UUID usuarioId) {
        Cart cart = getCartByUsuarioId(usuarioId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }
}
