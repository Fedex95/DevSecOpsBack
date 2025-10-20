package com.tienda.kpback.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="usuarios")

public class UsuarioEnt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String nombre;
    private String apellido;

    @Column(unique = true)
    private String usuario;
    @Column(unique=true)
    private String cedula;
    @Column(unique=true)
    private String email;

    private String pass;
    private String telefono;
    private String direccion;
    private String nombreTarjeta;
    private String numeroTarjeta;
    private String fechaValidez;
    private String cvv;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private Cart cart;

    @Enumerated(EnumType.STRING)
    private Rol rol;
    public enum Rol{
        ADMIN,
        USER
    }
}
