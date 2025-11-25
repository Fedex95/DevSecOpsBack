package com.tienda.kpback.Config;

import com.tienda.kpback.Entity.UsuarioEnt;
import com.tienda.kpback.Service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;  
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String uri = request.getRequestURI();
        if (uri.startsWith("/auth/login") || uri.startsWith("/auth/register")
                || uri.startsWith("/v3/api-docs") || uri.startsWith("/swagger-ui")) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String username = jwtService.extractUsername(token);
            UUID userId = jwtService.extractUserId(token);
            String rolStr = jwtService.extractRol(token);
            List<SimpleGrantedAuthority> authorities =
                    rolStr != null
                            ? List.of(new SimpleGrantedAuthority("ROLE_" + rolStr))
                            : List.of(new SimpleGrantedAuthority("ROLE_USER"));

            if (username != null && userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                CustomUserDetails principal =
                        new CustomUserDetails(userId, username, null,  
                                rolStr != null ? UsuarioEnt.Rol.valueOf(rolStr) : UsuarioEnt.Rol.USER);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(principal, null, authorities);  
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception ex) {
            System.out.println("Error al validar el token JWT: " + ex.getMessage());
        }

        chain.doFilter(request, response);
    }
}