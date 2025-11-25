package com.tienda.kpback.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore;  
@Entity
@Getter
@Setter
@Table(name = "detalles_prestamo")  
public class DetallePrestamo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)  
    @Column(columnDefinition = "uuid default gen_random_uuid()")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "prestamo_id")
    @JsonIgnore  
    private Prestamo prestamo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "libro_id", nullable = false) 
    private Libro libro;

    @Column(nullable = false)
    private int cantidad = 1;  
}
