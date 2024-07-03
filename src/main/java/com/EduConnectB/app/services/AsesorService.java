package com.EduConnectB.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EduConnectB.app.dao.AsesorRepository;
import com.EduConnectB.app.dao.SesionRepository;
import com.EduConnectB.app.models.Asesor;
import com.EduConnectB.app.models.EstadoSesion;
import com.EduConnectB.app.models.EstadoUsuario;
import com.EduConnectB.app.models.Sesion;
import com.EduConnectB.app.models.Usuario;

import jakarta.transaction.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AsesorService {

    @Autowired
    private AsesorRepository asesorRepository;
    
    @Autowired
    private SesionRepository sesionRepository;

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

    }

    @Transactional
    public void rechazarAsesor(Asesor asesor) {
        asesor.getUsuario().setEstado(EstadoUsuario.RECHAZADO); 
        asesorRepository.save(asesor);

    }
    
    public boolean estaDisponible(Asesor asesor, LocalDateTime fechaHora) {
        String horarioDisponibilidad = asesor.getHorarioDisponibilidad();
        DayOfWeek diaSemana = fechaHora.getDayOfWeek();
        LocalTime hora = fechaHora.toLocalTime();

        if (!horarioDisponibilidad.contains(diaSemana.toString()) || 
            !horarioDisponibilidad.contains(hora.getHour() + ":00")) {
            return false;
        }

        List<Sesion> sesiones = sesionRepository.findByAsesorAndFechaHoraBetweenAndEstadoNotIn(
                asesor, 
                fechaHora.minusMinutes(30),
                fechaHora.plusMinutes(30),
                List.of(EstadoSesion.CANCELADA, EstadoSesion.RECHAZADA)
        );

        return sesiones.isEmpty();
    }
}
