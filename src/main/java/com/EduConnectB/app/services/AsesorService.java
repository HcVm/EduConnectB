package com.EduConnectB.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.EduConnectB.app.dao.AsesorRepository;
import com.EduConnectB.app.models.Asesor;
import com.EduConnectB.app.models.EstadoUsuario;
import com.EduConnectB.app.models.Usuario;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AsesorService {

    @Autowired
    private AsesorRepository asesorRepository;
    
    @Autowired
    private JavaMailSender mailSender;

    public List<Asesor> obtenerTodosLosAsesores() {
        return asesorRepository.findAll();
    }

    public Optional<Asesor> obtenerAsesorPorId(Integer idAsesor) {
        return asesorRepository.findById(idAsesor);
    }

    public List<Asesor> obtenerAsesoresPorEspecialidad(String especialidad) {
        return asesorRepository.findByEspecialidad(especialidad);
    }

    public Asesor guardarAsesor(Asesor asesor) {
        return asesorRepository.save(asesor);
    }

    public void eliminarAsesor(Integer idAsesor) {
        asesorRepository.deleteById(idAsesor);
    }
    
    public Optional<Asesor> obtenerAsesorPorUsuario(Usuario usuario) {
        return asesorRepository.findByUsuario(usuario);
    }
    
    public Asesor actualizarAsesor(Integer idAsesor, Asesor asesorActualizado) {
        return asesorRepository.findById(idAsesor)
                .map(asesorExistente -> {
                    asesorExistente.setEspecialidad(asesorActualizado.getEspecialidad());
                    return asesorRepository.save(asesorExistente);
                })
                .orElseThrow(() -> new RuntimeException("Asesor no encontrado"));
    }
    
    public List<Asesor> obtenerAsesoresPorEstado(EstadoUsuario estado) {
        return asesorRepository.findByUsuarioEstado(estado);
    }
    
    @Transactional
    public void aprobarAsesor(Asesor asesor) {
        asesor.getUsuario().setEstado(EstadoUsuario.ACTIVO);
        asesorRepository.save(asesor);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(asesor.getUsuario().getCorreoElectronico());
        message.setSubject("¡Tu solicitud de asesor en EduConnect ha sido aprobada!");
        message.setText("Hola " + asesor.getUsuario().getNombre() + ",\n\n" +
                "Nos complace informarte que tu solicitud para ser asesor en EduConnect ha sido aprobada.\n" +
                "Ahora puedes iniciar sesión y comenzar a ofrecer tus servicios de asesoría.\n\n" +
                "¡Bienvenido a la comunidad de EduConnect!");
        mailSender.send(message);
    }

    @Transactional
    public void rechazarAsesor(Asesor asesor) {
        asesor.getUsuario().setEstado(EstadoUsuario.RECHAZADO); 
        asesorRepository.save(asesor);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(asesor.getUsuario().getCorreoElectronico());
        message.setSubject("Tu solicitud de asesor en EduConnect ha sido rechazada");
        message.setText("Hola " + asesor.getUsuario().getNombre() + ",\n\n" +
                "Lamentamos informarte que tu solicitud para ser asesor en EduConnect ha sido rechazada.\n" +
                "Si tienes alguna pregunta, puedes ponerte en contacto con nosotros.\n\n" +
                "Atentamente,\n" +
                "El equipo de EduConnect");
        mailSender.send(message);
    }
}
