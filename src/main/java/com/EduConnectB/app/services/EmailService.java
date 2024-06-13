package com.EduConnectB.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.EduConnectB.app.models.Usuario;

@Service
public class EmailService {
	
	@Autowired
    private JavaMailSender mailSender;

	
	public void enviarCorreoRestablecimientoContrasena(Usuario usuario, String token) {
	    String urlRestablecimiento = "http://tu-frontend/restablecer-contrasena/" + token; // CAMBIAR POR LA URL DEL FRONT

	    SimpleMailMessage message = new SimpleMailMessage();
	    message.setTo(usuario.getCorreoElectronico());
	    message.setSubject("Restablecimiento de contraseña de EduConnect");
	    message.setText("Hola " + usuario.getNombre() + ",\n\n" +
	            "Para restablecer tu contraseña, haz clic en el siguiente enlace:\n" + urlRestablecimiento);
	    mailSender.send(message);
	}

}
