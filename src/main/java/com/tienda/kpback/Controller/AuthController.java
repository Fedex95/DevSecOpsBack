package com.tienda.kpback.Controller;

import com.tienda.kpback.Config.CustomUserDetails;
import com.tienda.kpback.Entity.UsuarioEnt;
import com.tienda.kpback.Service.JwtService;
import com.tienda.kpback.Service.UsuarioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private JwtService jwtService;

    public static class LoginRequest {
        @Email(message = "Email inválido")
        @NotBlank(message = "Email es obligatorio")
        private String email;  
        @NotBlank(message = "Contraseña es obligatoria")
        private String pass;

        public String getEmail() { return email; }  
        public void setEmail(String email) { this.email = email; }
        public String getPass() { return pass; }
        public void setPass(String pass) { this.pass = pass; }
    }

    public static class RegisterRequest {
        @NotBlank(message = "Usuario es obligatorio")
        @Size(min = 3, max = 20, message = "Usuario debe tener entre 3 y 20 caracteres")
        private String usuario;
        @NotBlank(message = "Contraseña es obligatoria")
        @Size(min = 6, message = "Contraseña debe tener al menos 6 caracteres")
        private String pass;
        @NotBlank(message = "Nombre es obligatorio")
        private String nombre;
        @NotBlank(message = "Apellido es obligatorio")
        private String apellido;
        @NotBlank(message = "Cédula es obligatoria")
        private String cedula;
        @Email(message = "Email inválido")
        private String email;
        @NotBlank(message = "Teléfono es obligatorio")
        private String telefono;

        public String getUsuario() { return usuario; }
        public void setUsuario(String usuario) { this.usuario = usuario; }
        public String getPass() { return pass; }
        public void setPass(String pass) { this.pass = pass; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getApellido() { return apellido; }
        public void setApellido(String apellido) { this.apellido = apellido; }
        public String getCedula() { return cedula; }
        public void setCedula(String cedula) { this.cedula = cedula; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getTelefono() { return telefono; }
        public void setTelefono(String telefono) { this.telefono = telefono; }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        String usuario = request.getUsuario();

        if (usuarioService.getUsuarioByUsuario(usuario).isPresent()) {
            return ResponseEntity.status(400).body("Usuario ya existe");
        }

        try {
            UsuarioEnt newUser = new UsuarioEnt();
            newUser.setUsuario(usuario);
            newUser.setPass(request.getPass());
            newUser.setNombre(request.getNombre());
            newUser.setApellido(request.getApellido());
            newUser.setCedula(request.getCedula());
            newUser.setEmail(request.getEmail());
            newUser.setTelefono(request.getTelefono());
            newUser.setRol(UsuarioEnt.Rol.USER);

            usuarioService.saveUsuario(newUser);
            usuarioService.sendVerificationEmail(newUser); 
            return ResponseEntity.ok("Usuario registrado. Revisa tu email para verificar.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al registrar usuario: " + e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        Optional<UsuarioEnt> userOpt = usuarioService.getUsuarioByEmail(email);
        if (userOpt.isPresent()) {
            UsuarioEnt user = userOpt.get();
            if (code.equals(user.getVerificationCode())) {
                user.setVerified(true);
                user.setVerificationCode(null); 
                usuarioService.saveUsuario(user);
                return ResponseEntity.ok("Cuenta verificada exitosamente");
            }
        }
        return ResponseEntity.status(400).body("Código inválido");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest credentials) {
        String email = credentials.getEmail(); 
        String pass = credentials.getPass();

        Optional<UsuarioEnt> userOpt = usuarioService.getUsuarioByEmail(email); 
        if (userOpt.isPresent()) {
            UsuarioEnt user = userOpt.get();
            try {
                boolean passMatches = usuarioService.checkPass(pass, user.getPass());
                if (passMatches) {
                    if (!user.isVerified()) {
                        return ResponseEntity.status(403).body("Cuenta no verificada. Revisa tu email.");
                    }
                    Map<String, Object> extraClaims = new HashMap<>();
                    extraClaims.put("userId", user.getId().toString());
                    extraClaims.put("rol", user.getRol().name());
                    extraClaims.put("nombre", user.getNombre());

                    CustomUserDetails userDetails = new CustomUserDetails(
                            user.getId(),
                            user.getUsuario(),
                            user.getPass(),
                            user.getRol()
                    );

                    String token = jwtService.generateToken(extraClaims, userDetails);
                    String refreshToken = jwtService.generateRefreshToken(userDetails);
                    return ResponseEntity.ok(Map.of("token", token, "refreshToken", refreshToken));
                } else {
                    return ResponseEntity.status(401).body("Credenciales inválidas");
                }
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Error interno en autenticación");
            }
        } else {
            System.out.println("Usuario no encontrado");
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Refresh token requerido");
        }
        String refreshToken = authHeader.substring(7);
        try {
            String newAccessToken = jwtService.refreshAccessToken(refreshToken);
            return ResponseEntity.ok(Map.of("token", newAccessToken));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Refresh token inválido o expirado");
        }
    }
}