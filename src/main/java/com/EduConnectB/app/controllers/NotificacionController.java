package com.EduConnectB.app.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import com.EduConnectB.app.models.Notificacion;
import com.EduConnectB.app.services.NotificacionService;


@RestController
@RequestMapping("/educ/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;


    @GetMapping("/{userId}")
    public ResponseEntity<List<Notificacion>> getNotifications(@PathVariable Integer userId) {
        List<Notificacion> notifications = notificacionService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/{userId}/markAsRead/{notificationId}")
    public ResponseEntity<Void> markAsRead(@PathVariable Integer userId, @PathVariable Integer notificationId) {
        notificacionService.deleteNotification(userId, notificationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/markAllAsRead")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Integer userId) {
        notificacionService.deleteAllNotificationsByUserId(userId);
        return ResponseEntity.ok().build();
    }
}