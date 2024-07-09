package com.EduConnectB.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.EduConnectB.app.dao.UsuarioRepository;
import com.EduConnectB.app.models.Usuario;
import com.EduConnectB.app.services.EmailService;

@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/send")
    public String sendEmail(@RequestParam String fromUser, @RequestParam String toUser,
                            @RequestParam String subject, @RequestParam String body) {
        Usuario from = usuarioRepository.findByNombre(fromUser);
        Usuario to = usuarioRepository.findByNombre(toUser);

        if (from == null || to == null) {
            return "Usuario no encontrado";
        }
        
        String fullBody = "Correo enviado por: " + from.getNombre() + " (" + from.getCorreoElectronico() + ")\n\n" + body;

        emailService.sendSimpleMessage(to.getCorreoElectronico(), subject, fullBody);
        return "Correo enviado exitosamente";
    }
}