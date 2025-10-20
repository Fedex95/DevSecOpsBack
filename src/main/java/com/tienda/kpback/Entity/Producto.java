package com.tienda.kpback.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter

public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private String imagenURL;

    @Enumerated(EnumType.STRING)
    private Categoria categoria;
    public enum Categoria{
        Monitor, Mouse, Teclado, Case, Procesador, TarjetaGrafica, MemoriaRAM, MemoriaROM, PlacaMadre,
        Accesorios, FuentePoder, Ventilador
    }
}
