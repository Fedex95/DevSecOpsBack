package com.tienda.kpback.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Entity
@Table(name = "libros")  
@Getter
@Setter
public class Libro {  
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)  
    @Column(columnDefinition = "uuid default gen_random_uuid()")
    private UUID id;

    @Column(nullable = false)
    private String titulo; 

    @Column(nullable = false)
    private String autor;  

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Categoria categoria;  

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(unique = true, length = 20)
    private String isbn; 

    @Column(name = "imagen_url", columnDefinition = "TEXT")
    private String imagenUrl;  

    @Column(name = "copias_disponibles", nullable = false)
    private int copiasDisponibles = 1;

    public enum Categoria {
        Ficción, No_Ficción, Ciencia_Ficción, Fantasía, Misterio,
        Romance, Terror, Biografía, Historia, Infantil, Poesía, Drama
    }
}
