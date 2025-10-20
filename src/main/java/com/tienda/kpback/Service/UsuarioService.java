package com.tienda.kpback.Service;

import com.tienda.kpback.Config.Pass;
import com.tienda.kpback.Entity.Cart;
import com.tienda.kpback.Entity.UsuarioEnt;
import com.tienda.kpback.Repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<UsuarioEnt> getAllUsuarios(){
        return usuarioRepository.findAll();
    }

    @Transactional
    public Optional<UsuarioEnt> getUsuarioById(Long id){
        return usuarioRepository.findById(id);
    }

    @Transactional
    public Optional<UsuarioEnt> getUsuarioByUsuario(String usuario){
        return usuarioRepository.findByUsuario(usuario);
    }

    public UsuarioEnt saveUsuario(UsuarioEnt usuario){
        try{
            String encodedPass = Pass.encrip(usuario.getPass());
            usuario.setPass(encodedPass);

            if(usuario.getCart() == null){
                Cart cart = new Cart();
                cart.setUsuario(usuario);
                usuario.setCart(cart);
            }
            return usuarioRepository.save(usuario);
        }catch (Exception e){
            throw new RuntimeException("Error al guardar usuario");
        }
    }

    public void deleteUsuario(Long id){
        Optional<UsuarioEnt> usuario = usuarioRepository.findById(id);
        if(usuario.isPresent()){
            usuarioRepository.deleteById(id);
        }else{
            throw new RuntimeException("Usuario no encontrado");
        }
    }

    public UsuarioEnt updateUsuario(Long id, UsuarioEnt usuario){
        Optional<UsuarioEnt> existing = usuarioRepository.findById(id);
        if(existing.isPresent()){
            UsuarioEnt updatedUsuario = existing.get();
            updatedUsuario.setUsuario(usuario.getUsuario());
            updatedUsuario.setNombre(usuario.getNombre());
            updatedUsuario.setApellido(usuario.getApellido());
            updatedUsuario.setCedula(usuario.getCedula());
            try{
                if(!updatedUsuario.getPass().equals(usuario.getPass())){
                    String encodedPass = Pass.encrip(usuario.getPass());
                    updatedUsuario.setPass(encodedPass);
                }
            }catch (Exception e){
                throw new RuntimeException("Error al encriptar");
            }
            updatedUsuario.setRol(usuario.getRol());
            updatedUsuario.setEmail(usuario.getEmail());
            updatedUsuario.setTelefono(usuario.getTelefono());
            updatedUsuario.setDireccion(usuario.getDireccion());
            updatedUsuario.setNombreTarjeta(usuario.getNombreTarjeta());
            updatedUsuario.setNumeroTarjeta(usuario.getNumeroTarjeta());
            updatedUsuario.setFechaValidez(usuario.getFechaValidez());
            updatedUsuario.setCvv(usuario.getCvv());

            return usuarioRepository.save(updatedUsuario);
        }else{
            throw new RuntimeException("Usuario no encontrado");
        }
    }
    public boolean Admin(Long userId){
        return usuarioRepository.findById(userId)
                .map(usuario -> usuario.getRol() == UsuarioEnt.Rol.ADMIN)
                .orElse(false);
    }

    public boolean checkPass(String encodedPass, String storedHash) throws NoSuchAlgorithmException{
        String enteredPassHash = Pass.encrip(encodedPass);
        return storedHash.equals(enteredPassHash);
    }
}
