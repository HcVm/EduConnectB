package com.EduConnectB.app.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EduConnectB.app.models.Usuario;


@RestController
@RequestMapping("/ws")
public class NotificacionController {
	
	@Autowired
    private SimpMessagingTemplate messagingTemplate;
	
	@MessageMapping("/notificaciones")
    public void enviarNotificacion(@Payload String mensaje, Usuario usuario) {
        messagingTemplate.convertAndSendToUser(usuario.getCorreoElectronico(), "/topic/notificaciones", mensaje);
    }
}