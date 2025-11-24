package com.tienda.kpback.Entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Getter
@Setter
@Table(name="usuarios")

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UsuarioEnt {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)  
    @Column(columnDefinition = "uuid default gen_random_uuid()") 
    private UUID id;
    private String nombre;
    private String apellido;

    @Column(unique = true)
    private String usuario;
    @Column(unique=true)
    private String cedula;
    @Column(unique=true)
    private String email;
    @Column(unique=true)
    private String telefono;
    @JsonIgnore
    private String pass;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    private Cart cart;

    @Enumerated(EnumType.STRING)
    private Rol rol;
    public enum Rol{
        ADMIN,
        USER
    }

    @Column(nullable = true)  
    private boolean verified = false; 

    @Column
    private String verificationCode;  
}
