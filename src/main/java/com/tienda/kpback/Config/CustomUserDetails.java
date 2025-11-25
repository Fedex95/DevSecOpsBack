package com.tienda.kpback.Config;

import com.tienda.kpback.Entity.UsuarioEnt;
import lombok.Getter;  
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Getter  
public class CustomUserDetails implements UserDetails {
    private UUID userId;
    private String username;
    private String password;
    private UsuarioEnt.Rol rol;

    public CustomUserDetails(UUID userId, String username, String password, UsuarioEnt.Rol rol) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    public CustomUserDetails(UUID userId, String username, String password, Collection<? extends GrantedAuthority> authorities, UsuarioEnt.Rol rol) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}