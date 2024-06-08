package com.EduConnectB.app.controllers;


import com.EduConnectB.app.models.Calificacion;
import com.EduConnectB.app.models.RecursoBiblioteca;
import com.EduConnectB.app.models.Sesion;
import com.EduConnectB.app.models.Usuario;
import com.EduConnectB.app.models.Valoracion;
import com.EduConnectB.app.services.BibliotecaDigitalService;
import com.EduConnectB.app.services.CalificacionService;
import com.EduConnectB.app.services.SesionService;
import com.EduConnectB.app.services.ValoracionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/estudiantes")
@PreAuthorize("hasAnyAuthority('ESTUDIANTE_ESTANDAR', 'ESTUDIANTE_PRO')")
public class EstudianteController extends BaseController {

    @Autowired
    private CalificacionService calificacionService;

    @Autowired
    private SesionService sesionService;

    @Autowired
    private ValoracionService valoracionService;
    
    @Autowired
    private BibliotecaDigitalService bibliotecaDigitalService;

    @GetMapping("/{idEstudiante}/calificaciones")
    public ResponseEntity<List<Calificacion>> obtenerCalificaciones(@PathVariable Integer idEstudiante) {
        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        if (usuarioAutenticado != null && usuarioAutenticado.getIdUsuario().equals(idEstudiante)) {
            List<Calificacion> calificaciones = calificacionService.obtenerCalificacionesPorEstudiante(idEstudiante);
            return ResponseEntity.ok(calificaciones);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado. Solo puedes ver tus propias calificaciones.");
        }
    }

    @PostMapping("/sesiones")
    public ResponseEntity<Sesion> programarSesion(@RequestBody Sesion sesion) {
        // Validar datos de la sesión (e.g., disponibilidad del asesor)
        sesion.setUsuario(obtenerUsuarioAutenticado()); // Asignar el usuario autenticado
        Sesion nuevaSesion = sesionService.guardarSesion(sesion);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaSesion);
    }

    @GetMapping("/{idEstudiante}/sesiones")
    public ResponseEntity<List<Sesion>> obtenerSesionesProgramadas(@PathVariable Integer idEstudiante) {
        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        if (usuarioAutenticado != null && usuarioAutenticado.getIdUsuario().equals(idEstudiante)) {
            List<Sesion> sesiones = sesionService.obtenerSesionesPorUsuario(idEstudiante);
            return ResponseEntity.ok(sesiones);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado. Solo puedes ver tus propias sesiones.");
        }
    }

    @PostMapping("/sesiones/{idSesion}/valoraciones")
    public ResponseEntity<Valoracion> valorarSesion(@PathVariable Integer idSesion, @RequestBody Valoracion valoracion) {
        // Validar si el estudiante participó en la sesión y si ya la valoró
        valoracion.setUsuario(obtenerUsuarioAutenticado()); // Asignar el usuario autenticado
        Valoracion nuevaValoracion = valoracionService.guardarValoracion(valoracion);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaValoracion);
    }
    
    @GetMapping("/biblioteca/{idRecurso}")
    @PreAuthorize("hasAuthority('ESTUDIANTE_PRO')") // Solo estudiantes PRO tienen acceso
    public ResponseEntity<?> obtenerRecursoBiblioteca(@PathVariable String idRecurso) {
        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();

        if (usuarioAutenticado == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No estás autenticado.");
        }

        if (!bibliotecaDigitalService.tieneAccesoABiblioteca(usuarioAutenticado)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes acceso a este recurso.");
        }

        try {
            RecursoBiblioteca recurso = bibliotecaDigitalService.obtenerRecurso(idRecurso);

            if (recurso == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recurso no encontrado.");
            }

            return ResponseEntity.ok(recurso);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al obtener el recurso.", e);
        }
    }
    
    
}

