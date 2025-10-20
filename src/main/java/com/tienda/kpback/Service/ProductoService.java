package com.tienda.kpback.Service;

import com.tienda.kpback.Entity.Producto;
import com.tienda.kpback.Repository.ProductoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {
    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> findAllProductos(){
        return productoRepository.findAll();
    }

    public Optional<Producto> getProductoById(Long productoId){
        return productoRepository.findById(productoId);
    }

    public Producto saveProducto(Producto producto){
        return productoRepository.save(producto);
    }

    public void deleteProducto(Long id){
        productoRepository.deleteById(id);
    }

    @Transactional
    public Producto updateProducto(Producto producto){
        Optional <Producto> existing = productoRepository.findById(producto.getId());
        if(existing.isPresent()){
            Producto p = existing.get();
            p.setNombre(producto.getNombre());
            p.setDescripcion(producto.getDescripcion());
            p.setCategoria(producto.getCategoria());
            p.setPrecio(producto.getPrecio());
            p.setImagenURL(producto.getImagenURL());
            return productoRepository.save(p);
        }else{
            throw new RuntimeException("No se encontro el producto");
        }
    }

}
