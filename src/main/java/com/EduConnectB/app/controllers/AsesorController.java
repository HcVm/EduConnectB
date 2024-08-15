package com.EduConnectB.app.controllers;

import com.EduConnectB.app.dto.ActualizarAsesorRequest;
import com.EduConnectB.app.exceptions.AuthenticationRequiredException;
import com.EduConnectB.app.models.Asesor;
import com.EduConnectB.app.models.Sesion;
import com.EduConnectB.app.models.TipoUsuario;
import com.EduConnectB.app.models.Calificacion;
import com.EduConnectB.app.models.Informe;
import com.EduConnectB.app.models.Usuario;
import com.EduConnectB.app.models.Valoracion;
import com.EduConnectB.app.services.AsesorService;
import com.EduConnectB.app.services.CalificacionService;
import com.EduConnectB.app.services.InformeService;
import com.EduConnectB.app.services.SesionService;
import com.EduConnectB.app.services.UsuarioService;
import com.EduConnectB.app.services.ValoracionService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/asesores")
@PreAuthorize("hasAuthority('ASESOR')")
public class AsesorController extends BaseController {
	
	@Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AsesorService asesorService;

    @Autowired
    private SesionService sesionService;
    
    @Autowired
    private CalificacionService calificacionService;

    @Autowired
    private InformeService informeService;

    @Autowired
    private ValoracionService valoracionService;

    @GetMapping("/perfil")
    public ResponseEntity<Asesor> obtenerPerfil() {
        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        if (usuarioAutenticado != null) {
            Asesor asesor = asesorService.obtenerAsesorPorUsuario(usuarioAutenticado)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el perfil del asesor.")); 
            return ResponseEntity.ok(asesor);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No estás autenticado como asesor.");
        }
    }
    
    @PutMapping("/actualizar")
    public ResponseEntity<Asesor> actualizarPerfilAsesor(@Validated @RequestBody ActualizarAsesorRequest request, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        
        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        if (usuarioAutenticado == null || usuarioAutenticado.getTipoUsuario() != TipoUsuario.ASESOR) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para actualizar el perfil de asesor.");
        }

        Asesor asesor = asesorService.obtenerAsesorPorUsuario(usuarioAutenticado)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asesor no encontrado."));

        asesor.setEspecialidad(request.getEspecialidad());
        asesor.setHorarioDisponibilidad(request.getHorarioDisponibilidad());
        
        asesorService.guardarAsesor(asesor);
        return ResponseEntity.ok(asesor);
    }
    
    @GetMapping("/{idAsesor}/sesiones")
    public ResponseEntity<List<Sesion>> obtenerSesiones(@PathVariable Integer idAsesor) {
    	Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        if (usuarioAutenticado == null || usuarioAutenticado.getTipoUsuario() != TipoUsuario.ASESOR) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para actualizar el perfil de asesor.");
        }
        Asesor asesorAutenticado = asesorService.obtenerAsesorPorUsuario(usuarioAutenticado).get();
        if (asesorAutenticado.getIdAsesor().equals(idAsesor)) {
            List<Sesion> sesiones = sesionService.obtenerSesionesPorAsesor(idAsesor);
            return ResponseEntity.ok(sesiones);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado. Solo puedes ver tus propias sesiones.");
        }
    }
    
    @PostMapping("/finalizar/{idSesion}")
    public ResponseEntity<Void> finalizarSesion(@PathVariable Integer idSesion) {
        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        if (usuarioAutenticado == null) {
            throw new AuthenticationRequiredException("Se requiere autenticación para acceder a este recurso.");
        }
        
        Sesion sesion = sesionService.obtenerSesionPorId(idSesion)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sesión no encontrada"));

        if (!tienePermisoParaSesion(sesion, usuarioAutenticado)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para finalizar esta sesión.");
        }

        sesionService.finalizarSesion(idSesion, usuarioAutenticado);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/{idSesion}")
    public ResponseEntity<Void> cancelarSesion(@PathVariable Integer idSesion) {
        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        if (usuarioAutenticado == null) {
            throw new AuthenticationRequiredException("Se requiere autenticación para acceder a este recurso.");
        }
        
        Sesion sesion = sesionService.obtenerSesionPorId(idSesion)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sesión no encontrada"));

        if (!tienePermisoParaSesion(sesion, usuarioAutenticado)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para cancelar esta sesión.");
        }

        sesionService.cancelarSesion(idSesion, usuarioAutenticado);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{idAsesor}/horario")
    public ResponseEntity<String> obtenerHorarioDisponibilidad(@PathVariable Integer idAsesor) {
        Asesor asesor = asesorService.obtenerAsesorPorId(idAsesor)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asesor no encontrado."));
        return ResponseEntity.ok(asesor.getHorarioDisponibilidad());
    }

    @PutMapping("/{idAsesor}/horario")
    public ResponseEntity<Asesor> actualizarHorario(@PathVariable Integer idAsesor, @RequestBody String nuevoHorario) {
    Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
    if (usuarioAutenticado != null && usuarioAutenticado.getTipoUsuario() == TipoUsuario.ASESOR) {
        Asesor asesorAutenticado = asesorService.obtenerAsesorPorUsuario(usuarioAutenticado).get();
        if (asesorAutenticado.getIdAsesor().equals(idAsesor)) {
            Asesor asesor = asesorService.obtenerAsesorPorId(idAsesor)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asesor no encontrado."));
            asesor.setHorarioDisponibilidad(nuevoHorario);
            asesorService.guardarAsesor(asesor);
            return ResponseEntity.ok(asesor);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado. Solo puedes actualizar tu propio horario.");
        }
    } else {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No estás autenticado como asesor.");
    }
}

    @GetMapping("/{idAsesor}/valoraciones")
    public ResponseEntity<List<Valoracion>> obtenerValoraciones(@PathVariable Integer idAsesor) {
        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        if (usuarioAutenticado != null && usuarioAutenticado.getIdUsuario().equals(idAsesor)) {
            List<Valoracion> valoraciones = valoracionService.obtenerValoracionesPorAsesor(idAsesor);
            return ResponseEntity.ok(valoraciones);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado. Solo puedes ver tus propias valoraciones.");
        }
    }
    
    @PostMapping("/{idSesion}/calificaciones")
    @PreAuthorize("hasAuthority('ASESOR')")
    public ResponseEntity<Calificacion> ingresarCalificacion(
            @PathVariable Integer idSesion, 
            @Validated @RequestBody Calificacion calificacion, 
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(calificacion);
        }

        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        if (usuarioAutenticado == null || usuarioAutenticado.getTipoUsuario() != TipoUsuario.ASESOR) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para ingresar calificaciones.");
        }

        Sesion sesion = sesionService.obtenerSesionPorId(idSesion)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sesión no encontrada."));

        if (!sesion.getAsesor().getUsuario().getIdUsuario().equals(usuarioAutenticado.getIdUsuario())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para ingresar calificaciones para esta sesión.");
        }

        Usuario estudiante = usuarioService.obtenerUsuarioPorId(calificacion.getUsuario().getIdUsuario())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudiante no encontrado."));

        calificacion.setSesion(sesion);
        calificacion.setUsuario(estudiante);

        Calificacion nuevaCalificacion = calificacionService.guardarCalificacion(calificacion);
        return ResponseEntity.ok(nuevaCalificacion);
    }

    @PostMapping("/estudiantes/{idEstudiante}/informes")
    public ResponseEntity<?> ingresarInforme(
            @PathVariable Integer idEstudiante,
            @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate
     fechaFin,
            @Validated @RequestBody Informe informe,
            BindingResult bindingResult
             ) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors()); 
        }

        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        if (usuarioAutenticado == null || usuarioAutenticado.getTipoUsuario() != TipoUsuario.ASESOR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para ingresar informes.");
        }

        Usuario estudiante = usuarioService.obtenerUsuarioPorId(idEstudiante)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudiante no encontrado."));

        List<Calificacion> calificaciones = calificacionService.obtenerCalificacionesPorEstudianteYPeriodo(
                idEstudiante, fechaInicio, fechaFin);
        if (calificaciones.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontraron calificaciones en el período especificado.");
        }

        String contenidoInforme = generarContenidoInforme(calificaciones);
        informe.setContenido(contenidoInforme);
        informe.setEstudiante(estudiante);
        informe.setFecha(new Timestamp(System.currentTimeMillis()));
        Informe nuevoInforme = informeService.guardarInforme(informe);

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoInforme);
    }

    @GetMapping("/estudiantes")
    public ResponseEntity<List<Usuario>> obtenerEstudiantes() {
        List<Usuario> estudiantes = usuarioService.buscarPorTipoUsuario(TipoUsuario.ESTUDIANTE);
        return ResponseEntity.ok(estudiantes);
    }
    
    private String generarContenidoInforme(List<Calificacion> calificaciones) {
        StringBuilder sb = new StringBuilder();
        sb.append("Informe de calificaciones:\n\n");
        for (Calificacion calificacion : calificaciones) {
            sb.append("Materia: ").append(calificacion.getNombreMateria()).append("\n");
            sb.append("Calificación: ").append(calificacion.getCalificacion()).append("\n");
            sb.append("Fecha: ").append(calificacion.getFecha()).append("\n");
            sb.append("Comentario: ").append(calificacion.getComentario()).append("\n");
        }
        return sb.toString();
    }
}
