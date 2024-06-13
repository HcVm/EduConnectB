package com.EduConnectB.app.services;

import com.EduConnectB.app.models.Asesor;
import com.EduConnectB.app.models.TipoUsuario;
import com.EduConnectB.app.models.Usuario;
import com.EduConnectB.app.dao.UsuarioRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificacionService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public void enviarNotificacionNuevoAsesor(Asesor asesor) {
        List<String> correosAdministradores = usuarioRepository.findByTipoUsuario(TipoUsuario.ADMIN)
                .stream()
                .map(Usuario::getCorreoElectronico)
                .toList();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("unlimitededuconnect@gmail.com");
        message.setTo(correosAdministradores.toArray(new String[0]));
        message.setSubject("Nuevo asesor registrado en EduConnect");
        message.setText("Se ha registrado un nuevo asesor:\n\n" +
                "Nombre: " + asesor.getUsuario().getNombre() + "\n" +
                "Correo electrónico: " + asesor.getUsuario().getCorreoElectronico() + "\n" +
                "Especialidad: " + asesor.getEspecialidad() + "\n");

        mailSender.send(message);
    }
}
