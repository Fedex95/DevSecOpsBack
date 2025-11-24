package com.tienda.kpback.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "prestamos")
public class Historial {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)  
    @Column(columnDefinition = "uuid default gen_random_uuid()")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id") 
    private UsuarioEnt usuario;

    @Column(name = "fecha_solicitud") 
    private LocalDateTime fecha; 

    @Enumerated(EnumType.STRING)
    private Prestamo.Estado estado;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "prestamo_id")
    private List<DetallePrestamo> detalles;
}
