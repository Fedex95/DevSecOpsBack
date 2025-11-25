package com.tienda.kpback.Controller;

import com.tienda.kpback.Entity.UsuarioEnt;
import com.tienda.kpback.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {
    private static final String INVALID_CREDENTIALS = "Invalid credentials";
    private static final String PASS_REGEX = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?~`]{8,}$";
    private static final String[] FORBIDDEN = {"case","randomblob","when","then","else","union","select","insert","update","delete","drop","exec","script","<",">","eval","function","blob","random"};

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("get/all")
    public ResponseEntity<List<UsuarioEnt>> getAllUsuarios(){
        List<UsuarioEnt> usuarios = usuarioService.getAllUsuarios();
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    @GetMapping("/getUsuario/{id}")
    public ResponseEntity<UsuarioEnt> getUsuarioById(@PathVariable UUID id){  
        Optional<UsuarioEnt> usuario = usuarioService.getUsuarioById(id);
        return usuario.map(value-> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/createUsuario")
    public ResponseEntity<String> createUsuario(@RequestParam String nombre, @RequestParam String apellido, @RequestParam String email, @RequestParam String pass) {
        try {
            UsuarioEnt usuario = new UsuarioEnt(); 
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setEmail(email);
            usuario.setPass(pass);
            usuarioService.saveUsuario(usuario);
            return ResponseEntity.ok("Usuario created");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid input");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating user");
        }
    }

    @PutMapping("/editUsuario/{id}")
    public ResponseEntity<UsuarioEnt> editUsuario(@PathVariable UUID id, @RequestBody UsuarioEnt usuario){ 
        try{
            UsuarioEnt editUsuario = usuarioService.updateUsuario(id, usuario);
            return new ResponseEntity<>(editUsuario, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/deleteUsuario/{id}")
    public ResponseEntity<UsuarioEnt> deleteUsuario(@PathVariable UUID id){ 
        try{
            usuarioService.deleteUsuario(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/viewPass")
    public ResponseEntity<String> viewPass(@RequestParam String email, @RequestParam String pass){
        if (email == null || email.isBlank() || pass == null || pass.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_CREDENTIALS);
        }
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$") || !pass.matches(PASS_REGEX)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_CREDENTIALS);
        }
        String pl = pass.toLowerCase();
        for (String w : FORBIDDEN) {
            if (pl.contains(w)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_CREDENTIALS);
        }
        Optional<UsuarioEnt> usuarioP = usuarioService.getUsuarioByEmailAndPass(email, pass); 
        if (usuarioP.isPresent()) {
            return new ResponseEntity<>("Correct Password", HttpStatus.OK); 
        } else {
            return new ResponseEntity<>(INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("{id}/admin")
    public ResponseEntity<Boolean> Admin(@PathVariable UUID id){ 
        boolean Admin = usuarioService.Admin(id);
        return ResponseEntity.ok(Admin);
    }
}
