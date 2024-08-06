package com.EduConnectB.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EduConnectB.app.dao.CalificacionRepository;
import com.EduConnectB.app.models.Calificacion;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CalificacionService {

    @Autowired
    private CalificacionRepository calificacionRepository;

    public List<Calificacion> obtenerTodasLasCalificaciones() {
        return calificacionRepository.findAll();
    }

    public Optional<Calificacion> obtenerCalificacionPorId(Integer idCalificacion) {
        return calificacionRepository.findById(idCalificacion);
    }
    
    public List<Calificacion> obtenerCalificacionesPorEstudiante(Integer idEstudiante) {
        return calificacionRepository.findByUsuarioIdUsuario(idEstudiante);
    }

    public Double calcularPromedioCalificacionesPorMateria(Integer idUsuario, String nombreMateria) {
        return calificacionRepository.calcularPromedioCalificacionesPorMateria(idUsuario, nombreMateria);
    }

    public Calificacion guardarCalificacion(Calificacion calificacion) {
        return calificacionRepository.save(calificacion);
    }

    public void eliminarCalificacion(Integer idCalificacion) {
        calificacionRepository.deleteById(idCalificacion);
    }
    
    public List<Calificacion> obtenerCalificacionesPorEstudianteYPeriodo(
            Integer idEstudiante, LocalDate fechaInicio, LocalDate fechaFin) {
        
        return calificacionRepository.findCalificacionesByEstudianteAndFechaBetween(idEstudiante, fechaInicio, fechaFin);
    }
}
