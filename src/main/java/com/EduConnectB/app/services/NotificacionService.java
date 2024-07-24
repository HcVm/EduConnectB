package com.EduConnectB.app.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EduConnectB.app.dao.NotificacionRepository;
import com.EduConnectB.app.models.Notificacion;
import com.EduConnectB.app.models.Sesion;
import com.EduConnectB.app.models.Usuario;


@Service
public class NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    public void enviarNotificacionCancelacionSesion(Usuario destinatario, Sesion sesion) {
        String mensaje = String.format(
                "La sesión programada para el %s ha sido cancelada.",
                sesion.getFechaHora().toString()
        );

        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(destinatario);
        notificacion.setMensaje(mensaje);
        notificacion.setFechaHora(LocalDateTime.now());
        notificacion.setLeido(false);
        notificacionRepository.save(notificacion);

    }
}
