package com.tienda.kpback.Service;

import com.tienda.kpback.Entity.*;
import com.tienda.kpback.Repository.CartItemRepository;
import com.tienda.kpback.Repository.CartRepository;
import com.tienda.kpback.Repository.HistorialRepository;
import com.tienda.kpback.Repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private HistorialService historialService;

    @Autowired
    private HistorialRepository historialRepository;

    @Autowired
    private NotificacionesService  notificacionesService;

    public Cart createCarrito(Long usuarioId){
        UsuarioEnt usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(()-> new RuntimeException("Usuario no encontrado"));

        return cartRepository.findByUsuario(usuario)
                .orElseGet(()-> {
                    Cart newcart = new Cart();
                    newcart.setUsuario(usuario);
                    return cartRepository.save(newcart);
                });
    }

    public Cart addItemToCart(Long usuarioId, Long productoId, int cantidad){
        Cart cart = createCarrito(usuarioId);
        Producto producto = productoService.getProductoById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProducto(producto);
        item.setCantidad(cantidad);

        cart.getItems().add(item);

        cartItemRepository.save(item);
        cart = cartRepository.save(cart);

        UsuarioEnt usuario = cart.getUsuario();
        if(usuario != null){
            usuario.setCart(cart);
            usuarioRepository.save(usuario);
        }
        return cart;
    }

    public Cart updateItemCart(Long cartItemId, int cantidad){
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(()-> new RuntimeException("Item no encontrado"));
        item.setCantidad(cantidad);
        cartItemRepository.save(item);
        return item.getCart();
    }

    public void deleteItemCart(Long cartId, Long itemId){
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(()-> new RuntimeException("Carrito no encontrado"));

        CartItem itemDelete = cart.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(()-> new RuntimeException("Item no encontrado en el carrito"));

        cart.getItems().remove(itemDelete);

        cartItemRepository.delete(itemDelete);

        cartRepository.save(cart);
    }

    @Transactional
    public void Pago(Long cartId){
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(()-> new RuntimeException("Carrito no encontrado"));

        double total = calcularTot(cart);
        System.out.println(total);

        boolean pagado = realizarPago(total);

        if(pagado){
            Historial historial = historialService.addCompra(cart);

            cart.getItems().clear();

            cartRepository.save(cart);

            String mta = "Compra realizada por" + cart.getUsuario().getNombre() +
                    " , ID del pedido: " + historial.getId();
            notificacionesService.notificarAdmin(cart.getUsuario(), mta);

            System.out.println("Pago exitoso //");
        }else {
            throw new RuntimeException("Error al realizar Pago");
        }
    }

    private double calcularTot(Cart cart){
        double total = 0.0;
        for(CartItem item : cart.getItems()){
            total += item.getProducto().getPrecio() * item.getCantidad();
        }
        return total;
    }
    private boolean realizarPago(double total){
        return true;
    }
}
