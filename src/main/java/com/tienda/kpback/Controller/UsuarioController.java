package com.tienda.kpback.Controller;

import com.tienda.kpback.Entity.UsuarioEnt;
import com.tienda.kpback.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;  
import java.util.List;
import java.util.UUID;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {
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
    public ResponseEntity<UsuarioEnt> createUsuario(@RequestBody UsuarioEnt usuario){
        UsuarioEnt newUsuario = usuarioService.saveUsuario(usuario);
        return new ResponseEntity<>(newUsuario, HttpStatus.CREATED);
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
    public ResponseEntity<String> viewPass(@RequestParam @Valid String email, @RequestParam @Valid String pass){
        // Validación básica
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$") || !pass.matches("^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?~`]{8,}$")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        // Deny list expandida
        String[] forbiddenWords = {"case", "randomblob", "when", "then", "else", "union", "select", "insert", "update", "delete", "drop", "exec", "script", "<", ">", "script", "eval", "function", "blob", "random"};
        for (String word : forbiddenWords) {
            if (pass.toLowerCase().contains(word)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        }

        Optional<UsuarioEnt> usuarioP = usuarioService.getUsuarioByEmailAndPass(email, pass); 
        if (usuarioP.isPresent()) {
            return new ResponseEntity<>("Correct Password", HttpStatus.OK); 
        } else {
            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("{id}/admin")
    public ResponseEntity<Boolean> Admin(@PathVariable UUID id){ 
        boolean Admin = usuarioService.Admin(id);
        return ResponseEntity.ok(Admin);
    }
}
