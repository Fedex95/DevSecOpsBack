package com.tienda.kpback.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name="notificaciones")

public class Notificaciones {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="usuarioid", referencedColumnName = "id")
    private UsuarioEnt usuario;

    private String texto;
    private LocalDate fechaNot = LocalDate.now();

    @Enumerated(EnumType.STRING)
    private destinoNotificacion destinoNotificacion;
    public enum destinoNotificacion{
        ADMIN,
        USER
    }
}
