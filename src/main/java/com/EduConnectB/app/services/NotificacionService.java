package com.EduConnectB.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EduConnectB.app.dao.NotificacionRepository;
import com.EduConnectB.app.models.Notificacion;

import java.util.List;
import java.util.Optional;

@Service
public class NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    public List<Notificacion> obtenerTodasLasNotificaciones() {
        return notificacionRepository.findAll();
    }

    public Optional<Notificacion> obtenerNotificacionPorId(Integer idNotificacion) {
        return notificacionRepository.findById(idNotificacion);
    }

    public Notificacion guardarNotificacion(Notificacion mensaje) {
        return notificacionRepository.save(mensaje);
    }

    public void eliminarNotificacion(Integer idNotificacion) {
    	notificacionRepository.deleteById(idNotificacion);
    }
}
