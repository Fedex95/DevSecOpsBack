package com.tienda.kpback.Service;

import com.tienda.kpback.Entity.UsuarioEnt;
import com.tienda.kpback.Repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;  
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender; 
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;  

@Service
public class UsuarioService {
    private static final String BCRYPT_REGEX = "^\\$2[aby]\\$\\d{2}\\$[./A-Za-z0-9]{53}$";
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private boolean isBCrypt(String v) {
        return v != null && v.matches(BCRYPT_REGEX);
    }

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JavaMailSender mailSender;  

    public List<UsuarioEnt> getAllUsuarios(){
        return usuarioRepository.findAll();
    }

    @Transactional
    public Optional<UsuarioEnt> getUsuarioById(UUID id){
        return usuarioRepository.findById(id);
    }

    @Transactional
    public Optional<UsuarioEnt> getUsuarioByUsuario(String usuario){
        return usuarioRepository.findByUsuario(usuario);
    }

    public Optional<UsuarioEnt> getUsuarioByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public UsuarioEnt saveUsuario(UsuarioEnt usuario){
        try{
            String pass = usuario.getPass();
            if (pass == null || pass.isBlank()) {
                throw new RuntimeException("Password vacío");
            }
            // Evita doble hashing
            if (!isBCrypt(pass)) {
                String encoded = passwordEncoder.encode(pass);
                usuario.setPass(encoded);
            } 
            return usuarioRepository.save(usuario);
        }catch (Exception e){
            throw new RuntimeException("Error al guardar usuario", e);
        }
    }

    public void deleteUsuario(UUID id){  
        Optional<UsuarioEnt> usuario = usuarioRepository.findById(id);
        if(usuario.isPresent()){
            usuarioRepository.deleteById(id);
        }else{
            throw new RuntimeException("Usuario no encontrado");
        }
    }

    public UsuarioEnt updateUsuario(UUID id, UsuarioEnt usuario){
        Optional<UsuarioEnt> existing = usuarioRepository.findById(id);
        if(existing.isPresent()){
            UsuarioEnt updatedUsuario = existing.get();
            updatedUsuario.setUsuario(usuario.getUsuario());
            updatedUsuario.setNombre(usuario.getNombre());
            updatedUsuario.setApellido(usuario.getApellido());
            updatedUsuario.setCedula(usuario.getCedula());

            String newPass = usuario.getPass();
            if (newPass != null && !newPass.isBlank()) {
                // Solo re-encodea si viene en plano
                if (!isBCrypt(newPass)) {
                    updatedUsuario.setPass(passwordEncoder.encode(newPass));
                } 
            }
            updatedUsuario.setRol(usuario.getRol());
            updatedUsuario.setEmail(usuario.getEmail());
            updatedUsuario.setTelefono(usuario.getTelefono());
            return usuarioRepository.save(updatedUsuario);
        }else{
            throw new RuntimeException("Usuario no encontrado");
        }
    }

    public boolean Admin(UUID userId){
        return usuarioRepository.findById(userId)
                .map(usuario -> usuario.getRol() == UsuarioEnt.Rol.ADMIN)
                .orElse(false);
    }

    public boolean checkPass(String plainPass, String storedHash){
        return passwordEncoder.matches(plainPass, storedHash);
    }

    public void sendVerificationEmail(UsuarioEnt user) {
        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase(); 
        user.setVerificationCode(code);
        user.setVerified(false);
        usuarioRepository.save(user);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Verificación de cuenta");
        message.setText("Tu código de verificación es: " + code);
        mailSender.send(message);
    }
}
