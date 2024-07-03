package com.EduConnectB.app.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EduConnectB.app.dao.AsesorRepository;
import com.EduConnectB.app.dao.SesionRepository;
import com.EduConnectB.app.models.Asesor;
import com.EduConnectB.app.models.EstadoSesion;
import com.EduConnectB.app.models.EstadoUsuario;
import com.EduConnectB.app.models.Usuario;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AsesorService {

    @Autowired
    private AsesorRepository asesorRepository;
    
    @Autowired
    private SesionRepository sesionRepository;
    
    private static final Logger log = LoggerFactory.getLogger(AsesorService.class);

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
        String horarioJson = asesor.getHorarioDisponibilidad();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, List<String>> horario = objectMapper.readValue(horarioJson, new TypeReference<>() {});

            DayOfWeek diaSemana = fechaHora.getDayOfWeek();
            String diaSemanaStr = convertirDiaSemanaAIngles(diaSemana);

            if (!horario.containsKey(diaSemanaStr)) {
                return false;
            }

            LocalTime horaSolicitada = fechaHora.toLocalTime();
            for (String rangoHorario : horario.get(diaSemanaStr)) {
                String[] horas = rangoHorario.split("-");
                if (horas.length == 2) { 
                    LocalTime horaInicio = LocalTime.parse(horas[0].trim());
                    LocalTime horaFin = LocalTime.parse(horas[1].trim());

                    if (!horaSolicitada.isBefore(horaInicio) && !horaSolicitada.isAfter(horaFin)) {
                        LocalDateTime inicioRango = fechaHora.minusMinutes(30);
                        LocalDateTime finRango = fechaHora.plusMinutes(30);
                        boolean tieneSesionesEnElRango = sesionRepository.existsByAsesorAndFechaHoraBetweenAndEstadoNotIn(
                                asesor, inicioRango, finRango, List.of(EstadoSesion.CANCELADA, EstadoSesion.RECHAZADA)
                        );
                        if (!tieneSesionesEnElRango) {
                            return true;
                        }
                    }
                } else {
                    log.warn("Formato de rango de horario inválido: {}", rangoHorario);
                }
            }
        } catch (JsonProcessingException e) {
            log.error("Error al procesar el horario de disponibilidad del asesor: {}", e.getMessage());
        }

        return false;
    }

    private String convertirDiaSemanaAIngles(DayOfWeek diaSemana) {
        return switch (diaSemana) {
            case MONDAY -> "lunes";
            case TUESDAY -> "martes";
            case WEDNESDAY -> "miercoles";
            case THURSDAY -> "jueves";
            case FRIDAY -> "viernes";
            case SATURDAY -> "sabado";
            case SUNDAY -> "domingo";
        };
    }
}
