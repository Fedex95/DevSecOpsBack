package com.tienda.kpback.Service;

import com.tienda.kpback.Entity.Cart;
import com.tienda.kpback.Entity.Historial;
import com.tienda.kpback.Entity.PedidoDetalle;
import com.tienda.kpback.Repository.HistorialRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service

public class HistorialService {
    @Autowired
    private HistorialRepository historialRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private NotificacionesService notificacionesService;

    @Transactional
    public Historial addCompra(Cart cart){
        Historial historial = new Historial();
        historial.setUsuario(cart.getUsuario());
        historial.setFecha(LocalDate.now());

        List<PedidoDetalle> detalles = cart.getItems().stream().map(item ->{
            PedidoDetalle detalle = new PedidoDetalle();
            detalle.setNombreProducto(item.getProducto().getNombre());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecio(item.getProducto().getPrecio());
            return detalle;
        }).collect(Collectors.toList());

        historial.setDetalles(detalles);

        historial = historialRepository.save(historial);

        return historial;
    }

    public List<Historial> findByUsuarioId(Long usuarioId){
        return historialRepository.findByUsuarioId(usuarioId);
    }

    public List<Historial> getHistorialAdmin(Long userId){
        if(usuarioService.Admin(userId)){
            return historialRepository.findAll();
        }else{
            throw new RuntimeException("Acceso denegado");
        }
    }
}
