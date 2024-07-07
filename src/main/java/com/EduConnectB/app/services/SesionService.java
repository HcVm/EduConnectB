package com.EduConnectB.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.EduConnectB.app.dao.SesionRepository;
import com.EduConnectB.app.models.EstadoSesion;
import com.EduConnectB.app.models.Sesion;
import com.EduConnectB.app.models.Usuario;

import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
public class SesionService {

    @Autowired
    private SesionRepository sesionRepository;
    
    @Autowired
    private JitsiService jitsiService;
    
    @Autowired
    private MembresiaService membresiaService;

    public List<Sesion> obtenerTodasLasSesiones() {
        return sesionRepository.findAll();
    }

    public Optional<Sesion> obtenerSesionPorId(Integer idSesion) {
        return sesionRepository.findById(idSesion);
    }

    public List<Sesion> obtenerSesionesPorUsuario(Integer idUsuario) {
        return sesionRepository.findByUsuarioIdUsuario(idUsuario);
    }

    public List<Sesion> obtenerSesionesPorAsesor(Integer idAsesor) {
        return sesionRepository.findByAsesorIdAsesor(idAsesor);
    }

    @Transactional
    public Sesion guardarSesion(Sesion sesion) {
        if (sesion.getUsuario() != null && !membresiaService.tieneMembresiaActiva(sesion.getUsuario())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El estudiante no tiene una membresía válida para programar sesiones.");
        }
        return sesionRepository.save(sesion); 
    }
    

	@Transactional
    public void aceptarSolicitudSesion(Sesion sesion) {
        sesion.setEstado(EstadoSesion.PROGRAMADA);
        sesion.setUrlJitsi(jitsiService.generarUrlSala(sesion.getIdSesion()));
        sesionRepository.save(sesion);
    }

    @Transactional
    public void rechazarSolicitudSesion(Sesion sesion) {
        sesion.setEstado(EstadoSesion.RECHAZADA);
        sesionRepository.save(sesion);
    }

    @Transactional
    public void cancelarSesion(Integer idSesion, Usuario usuarioAutenticado) {
        Sesion sesion = sesionRepository.findById(idSesion)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sesión no encontrada"));

        if (sesion.getUsuario().equals(usuarioAutenticado) || 
            sesion.getAsesor().getUsuario().getIdUsuario().equals(usuarioAutenticado.getIdUsuario())) {
            sesion.setEstado(EstadoSesion.CANCELADA);
            sesionRepository.save(sesion);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para cancelar esta sesión.");
        }
    }
    
    public boolean asesorTuvoSesionesConEstudianteEnPeriodo(Integer idAsesor, Integer idEstudiante, YearMonth mesAnio) {
        LocalDate inicioMes = mesAnio.atDay(1);
        LocalDate finMes = mesAnio.atEndOfMonth();
        return sesionRepository.existsByAsesorIdAsesorAndUsuarioIdUsuarioAndFechaHoraBetween(idAsesor, idEstudiante, inicioMes, finMes);
    }
}
