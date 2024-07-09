package com.EduConnectB.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.EduConnectB.app.models.Usuario;

@Controller
public class NotificacionController {
	
	@Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/notificaciones") // Endpoint para recibir mensajes
    public void enviarNotificacion(@Payload String mensaje, Usuario usuario) {
        messagingTemplate.convertAndSendToUser(usuario.getCorreoElectronico(), "/notificaciones", mensaje); // Envía la notificación al usuario específico
    }

}
