package com.tienda.kpback.Service;

import com.tienda.kpback.Entity.*;
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
        
        return cartRepository.save(cart);
    }

    public void deleteItemCart(UUID usuarioId, UUID itemId) {  
        UsuarioEnt usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Cart cart = cartRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(UUID usuarioId) {
        Cart cart = getCartByUsuarioId(usuarioId);
        cartRepository.save(cart);
    }
}
