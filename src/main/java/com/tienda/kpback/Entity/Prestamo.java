package com.tienda.kpback.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Getter
@Setter
@Table(name = "prestamos")  
public class Prestamo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)  
    @Column(columnDefinition = "uuid default gen_random_uuid()")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false) 
    private UsuarioEnt usuario;  

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud = LocalDateTime.now();  

    @Column(name = "fecha_retiro")
    private LocalDateTime fechaRetiro;

    @Column(name = "fecha_devolucion")
    private LocalDateTime fechaDevolucion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.pendiente;

    public enum Estado {
        pendiente, listo, retirado, devuelto
    }
}
