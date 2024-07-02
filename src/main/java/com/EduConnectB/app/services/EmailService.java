package com.EduConnectB.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.EduConnectB.app.models.Asesor;
import com.EduConnectB.app.models.Usuario;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
	
	@Autowired
    private JavaMailSender mailSender;

	
	public void enviarCorreoRestablecimientoContrasena(Usuario usuario, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(usuario.getCorreoElectronico());
            helper.setSubject("Restablecimiento de contraseña de EduConnect");

            String urlRestablecimiento = "http://tu-frontend/restablecer-contrasena/" + token;
            String htmlContent = "<html><body style='text-align: center; font-family: sans-serif; max-width: 600px; margin: 0 auto;'>" +
                    "<div style='background-color: #f0f0f0; padding: 20px;'>" +
                    "<img src='cid:logo' alt='Logo EduConnectB' style='width: 200px; height: auto; display: block; margin: 0 auto;' />" +
                    "</div>" +
                    "<div style='padding: 20px;'>" +
                    "<h1 style='font-size: 24px; margin-bottom: 20px;'>Restablecimiento de contraseña</h1>" +
                    "<p>Hola " + usuario.getNombre() + ",</p>" +
                    "<p>Hemos recibido una solicitud para restablecer tu contraseña.</p>" +
                    "<p>Si no solicitaste este cambio, puedes ignorar este correo electrónico. Tu contraseña seguirá siendo la misma.</p>" +
                    "<p>Para restablecer tu contraseña, haz clic en el siguiente enlace:</p>" +
                    "<p><a href='" + urlRestablecimiento + "' style='display: inline-block; padding: 10px 20px; background-color: #007bff; color: white; text-decoration: none; border-radius: 5px;'>Restablecer contraseña</a></p>" +
                    "<p>Este enlace expirará en 24 horas.</p>" +
                    "</div>" +
                    "<div style='background-color: #f0f0f0; padding: 20px;'>" +
                    "<img src='cid:banner' alt='Banner EduConnectB' style='width: 100%; max-width: 600px; height: auto;' />" +
                    "</div>" +
                    "</body></html>";

            helper.setText(htmlContent, true);

            ClassPathResource logoImage = new ClassPathResource("static/images/logoEduconnect.png");
            helper.addInline("logo", logoImage);

            ClassPathResource bannerImage = new ClassPathResource("static/images/resetpass.jpg");
            helper.addInline("banner", bannerImage);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace(); 
        }
    }
	
	public void enviarCorreoConfirmacionEstudiante(Usuario usuario) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(usuario.getCorreoElectronico());
            helper.setSubject("¡Bienvenido a EduConnect!");

            String htmlContent = "<html><body style='text-align: center; font-family: sans-serif; max-width: 600px; margin: 0 auto;'>" +
                    "<div style='background-color: #f0f0f0; padding: 20px;'>" +
                    "<img src='cid:logo' alt='Logo EduConnectB' style='width: 200px; height: auto; display: block; margin: 0 auto;' />" +
                    "</div>" +
                    "<div style='padding: 20px;'>" +
                    "<h1 style='font-size: 24px; margin-bottom: 20px;'>¡Bienvenido a EduConnect, " + usuario.getNombre() + "!</h1>" + // 
                    "<p style='margin-bottom: 10px;'>Estamos emocionados de que te hayas unido a nuestra comunidad de aprendizaje.</p>" +
                    "<p style='margin-bottom: 20px;'>En EduConnect, encontrarás una amplia variedad de recursos y herramientas para ayudarte a alcanzar tus metas académicas.</p>" +
                    "<p style='margin-bottom: 10px;'>Explora nuestra plataforma y descubre:</p>" +
                    "<ul style='text-align: left; margin-bottom: 20px;'>" +
                    "<li>Asesorías personalizadas con expertos en diversas áreas.</li>" +
                    "<li>Una biblioteca digital con miles de recursos educativos de calidad.</li>" +
                    "<li>Foros de discusión y grupos de estudio para conectar con otros estudiantes.</li>" +
                    "</ul>" +
                    "<p style='margin-bottom: 20px;'>Para comenzar a aprovechar al máximo tu experiencia en EduConnect, te invitamos a adquirir una membresía. Nuestras membresías te brindan acceso ilimitado a todos nuestros recursos y servicios premium.</p>" +
                    "<p><a href='http://tu-frontend/pricing' style='display: inline-block; padding: 10px 20px; background-color: #007bff; color: white; text-decoration: none; border-radius: 5px;'>Ver planes de membresía</a></p>" +
                    "</div>" +
                    "<div style='background-color: #f0f0f0; padding: 20px;'>" +
                    "<img src='cid:banner' alt='Banner EduConnectB' style='width: 100%; max-width: 600px; height: auto;' />" +
                    "</div>" +
                    "</body></html>";

            helper.setText(htmlContent, true);

            ClassPathResource logoImage = new ClassPathResource("static/images/logoEduconnect.png");
            helper.addInline("logo", logoImage);

            ClassPathResource bannerImage = new ClassPathResource("static/images/banneralumno.jpg");
            helper.addInline("banner", bannerImage);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace(); 
        }
    }

    public void enviarCorreoConfirmacionAsesor(Asesor asesor) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(asesor.getUsuario().getCorreoElectronico());
            helper.setSubject("Confirmación de solicitud de asesor en EduConnect");

            String htmlContent = "<html>" +
                    "<body style='text-align: center; font-family: sans-serif; max-width: 600px; margin: 0 auto;'>" +
                    "<div style='background-color: #f0f0f0; padding: 20px;'>" +
                    "<img src='cid:logo' alt='Logo EduConnectB' style='width: 200px; height: auto; display: block; margin: 0 auto;' />" +
                    "</div>" +
                    "<div style='padding: 20px;'>" +
                    "<h1 style='font-size: 24px; margin-bottom: 20px;'>¡Gracias por tu interés en ser asesor en EduConnect, " + asesor.getUsuario().getNombre() + "!</h1>" +
                    "<p style='margin-bottom: 10px;'>Hemos recibido tu solicitud y la estamos revisando cuidadosamente.</p>" +
                    "<p style='margin-bottom: 20px;'>Nuestro equipo evaluará tu perfil y experiencia para asegurarnos de que cumples con nuestros estándares de calidad.</p>" +
                    "<p style='margin-bottom: 20px;'>Te notificaremos por correo electrónico en los próximos días sobre el estado de tu solicitud.</p>" +
                    "<p>Mientras tanto, te invitamos a conocer más sobre EduConnect y los beneficios de ser parte de nuestra comunidad de expertos.</p>" +
                    "<p><a href='http://tu-frontend/nosotros' style='display: inline-block; padding: 10px 20px; background-color: #007bff; color: white; text-decoration: none; border-radius: 5px;'>Más información sobre EduConnect</a></p>" +
                    "</div>" +
                    "<div style='background-color: #f0f0f0; padding: 20px;'>" +
                    "<img src='cid:banner' alt='Banner EduConnectB' style='width: 100%; max-width: 600px; height: auto;' />" +
                    "</div>" +
                    "</body></html>";

            helper.setText(htmlContent, true);

            ClassPathResource logoImage = new ClassPathResource("static/images/logoEduconnect.png");
            helper.addInline("logo", logoImage);

            ClassPathResource bannerImage = new ClassPathResource("static/images/banneasesor.jpg");
            helper.addInline("banner", bannerImage);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace(); 
        }
    }

}
