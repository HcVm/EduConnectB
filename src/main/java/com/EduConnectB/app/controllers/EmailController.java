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
        Usuario from = usuarioRepository.findByNombreUsuario(fromUser);
        Usuario to = usuarioRepository.findByNombreUsuario(toUser);

        if (from == null || to == null) {
            return "Usuario no encontrado";
        }

        emailService.sendSimpleMessage(to.getCorreoElectronico(), subject, body);
        return "Correo enviado exitosamente";
    }
}