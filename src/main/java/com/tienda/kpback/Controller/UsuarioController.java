package com.tienda.kpback.Controller;


import com.tienda.kpback.Entity.UsuarioEnt;
import com.tienda.kpback.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")


public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("get/all")
    public ResponseEntity<List<UsuarioEnt>> getAllUsuarios(){
        List<UsuarioEnt> usuarios = usuarioService.getAllUsuarios();
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    @GetMapping("/getUsuario/{id}")
    public ResponseEntity<UsuarioEnt> getUsuarioById(@PathVariable Long id){
        Optional<UsuarioEnt> usuario = usuarioService.getUsuarioById(id);
        return usuario.map(value-> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/createUsuario")
    public ResponseEntity<UsuarioEnt> createUsuario(@RequestBody UsuarioEnt usuario){
        UsuarioEnt newUsuario = usuarioService.saveUsuario(usuario);
        return new ResponseEntity<>(newUsuario, HttpStatus.CREATED);
    }

    @PutMapping("/editUsuario/{id}")
    public ResponseEntity<UsuarioEnt> editUsuario(@PathVariable Long id, @RequestBody UsuarioEnt usuario){
        try{
            UsuarioEnt editUsuario = usuarioService.updateUsuario(id, usuario);
            return new ResponseEntity<>(editUsuario, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/deleteUsuario/{id}")
    public ResponseEntity<UsuarioEnt> deleteUsuario(@PathVariable Long id){
        try{
            usuarioService.deleteUsuario(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/viewPass")
    public ResponseEntity<String> viewPass(@RequestParam String usuario, @RequestParam String pass){
        Optional<UsuarioEnt> usuarioP = usuarioService.getUsuarioByUsuario(usuario);
        if(usuarioP.isPresent()){
            UsuarioEnt usuarioExist = usuarioP.get();
            try{
                boolean correctPass = usuarioService.checkPass(pass, usuarioExist.getPass());
                if (correctPass){
                    return new ResponseEntity<>("Correct Password", HttpStatus.OK);
                }else {
                    return new ResponseEntity<>("Wrong Password", HttpStatus.UNAUTHORIZED);
                }
            }catch (NoSuchAlgorithmException e){
                return new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            return new ResponseEntity<>("Usuario no existente", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("{id}/admin")
    public ResponseEntity<Boolean> Admin(@PathVariable Long id){
        boolean Admin = usuarioService.Admin(id);
        return ResponseEntity.ok(Admin);
    }
}
