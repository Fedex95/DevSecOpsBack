package com.tienda.kpback.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "carritos")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)  
    @Column(columnDefinition = "uuid default gen_random_uuid()")
    private UUID id;

    @OneToOne
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private UsuarioEnt usuario;
}
