package com.tienda.kpback.Service;

import com.tienda.kpback.Entity.Notificaciones;
import com.tienda.kpback.Entity.UsuarioEnt;
import com.tienda.kpback.Repository.NotificacionesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificacionesService {
    @Autowired
    private NotificacionesRepository notificacionesRepository;

    public void notificarAdmin(UsuarioEnt usuario, String texto){
        Notificaciones notificacion = new Notificaciones();
        notificacion.setUsuario(usuario);
        notificacion.setTexto(texto);
        notificacion.setDestinoNotificacion(Notificaciones.destinoNotificacion.ADMIN);
        notificacionesRepository.save(notificacion);
    }
}
