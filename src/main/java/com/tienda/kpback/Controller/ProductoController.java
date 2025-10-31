package com.tienda.kpback.Controller;

import com.tienda.kpback.Entity.Producto;
import com.tienda.kpback.Repository.ProductoRepository;
import com.tienda.kpback.Service.ProductoService;
import com.tienda.kpback.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/producto")


public class ProductoController {
    @Autowired
    private ProductoService productoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping("/find/all")
    public ResponseEntity<List<Producto>> findAllProductos() {
        List<Producto> productos = productoRepository.findAll();
        return new ResponseEntity<>(productos, HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    public Optional<Producto> getProductoById(@PathVariable Long id){
        return productoService.getProductoById(id);
    }

    @PostMapping("/newproducto")
    public ResponseEntity<Producto> createProducto(@RequestParam Long userId, @RequestBody Producto producto){
        if(usuarioService.Admin(userId)){
            Producto createdPr = productoService.saveProducto(producto);
            return new ResponseEntity<>(createdPr, HttpStatus.CREATED);
        }else{
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Producto> updateProducto(@RequestParam Long userId, @PathVariable Long id, @RequestBody Producto producto){
        if(usuarioService.Admin(userId)){
            producto.setId(id);
            Producto createdPr = productoService.updateProducto(producto);
            return new ResponseEntity<>(createdPr, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProducto(@RequestParam Long userId, @PathVariable Long id){
        try{
            if(usuarioService.Admin(userId)){
                productoService.deleteProducto(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }else{
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
