package com.tienda.kpback.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnore; 

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "tickets") 
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)  
    @Column(columnDefinition = "uuid default gen_random_uuid()")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "prestamo_id")
    @JsonIgnore  
    private Prestamo prestamo;

    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision = LocalDateTime.now();
}
