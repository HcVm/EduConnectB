package com.EduConnectB.app.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EduConnectB.app.dao.NotificacionRepository;
import com.EduConnectB.app.dao.UsuarioRepository;
import com.EduConnectB.app.models.Notificacion;
import com.EduConnectB.app.models.Usuario;

@Service
public class NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Notificacion saveNotificacion(Notificacion notificacion) {
        return notificacionRepository.save(notificacion);
    }

    public List<Notificacion> getNotificationsByUserId(Integer userId) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return notificacionRepository.findByUsuario(usuario);
    }

    public void deleteNotification(Integer userId, Integer notificationId) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Notificacion notificacion = notificacionRepository.findByIdNotificacionAndUsuario(notificationId, usuario)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        notificacionRepository.delete(notificacion);
    }

    public void deleteAllNotificationsByUserId(Integer userId) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        List<Notificacion> notificaciones = notificacionRepository.findByUsuario(usuario);
        notificacionRepository.deleteAll(notificaciones);
    }
}
