package com.EduConnectB.app.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.EduConnectB.app.models.Usuario;

@Service
public class NotificacionService {
	
	private final Map<Integer, List<String>> notificacionesPorUsuario = new ConcurrentHashMap<>();

    public void enviarNotificacion(Usuario usuario, String mensaje) {
        List<String> notificaciones = notificacionesPorUsuario.getOrDefault(usuario.getIdUsuario(), new ArrayList<>());
        notificaciones.add(mensaje);
        notificacionesPorUsuario.put(usuario.getIdUsuario(), notificaciones);
    }

    public List<String> obtenerNotificaciones(Usuario usuario) {
        return notificacionesPorUsuario.getOrDefault(usuario.getIdUsuario(), Collections.emptyList());
    }

    public void marcarNotificacionesComoLeidas(Usuario usuario) {
        notificacionesPorUsuario.remove(usuario.getIdUsuario());
    }
    
}
