package com.EduConnectB.app.controllers;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.EduConnectB.app.dao.NotificacionRepository;
import com.EduConnectB.app.models.Notificacion;

import java.util.List;

@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @GetMapping("/{userId}")
    public ResponseEntity<List<Notificacion>> getNotificaciones(@PathVariable Integer userId) {
        List<Notificacion> notificaciones = notificacionRepository.findByUsuarioIdUsuario(userId);
        return ResponseEntity.ok(notificaciones);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Notificacion> marcarComoLeida(@PathVariable Integer id) {
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.SC_NOT_FOUND, "Notificación no encontrada", null));
        
        notificacion.setLeido(true);
        Notificacion notificacionActualizada = notificacionRepository.save(notificacion);
        return ResponseEntity.ok(notificacionActualizada);
    }
}
