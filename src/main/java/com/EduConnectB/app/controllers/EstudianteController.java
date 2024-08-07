package com.EduConnectB.app.controllers;


import com.EduConnectB.app.dto.ActualizarUsuarioRequest;
import com.EduConnectB.app.exceptions.AuthenticationRequiredException;
import com.EduConnectB.app.models.Asesor;
import com.EduConnectB.app.models.Calificacion;
import com.EduConnectB.app.models.RecursoBiblioteca;
import com.EduConnectB.app.models.Sesion;
import com.EduConnectB.app.models.Usuario;
import com.EduConnectB.app.models.Valoracion;
import com.EduConnectB.app.services.AsesorService;
import com.EduConnectB.app.services.BibliotecaDigitalService;
import com.EduConnectB.app.services.CalificacionService;
import com.EduConnectB.app.services.SesionService;
import com.EduConnectB.app.services.UsuarioService;
import com.EduConnectB.app.services.ValoracionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/estudiantes")
@PreAuthorize("hasAnyAuthority('ESTUDIANTE')")
public class EstudianteController extends BaseController {

    @Autowired
    private CalificacionService calificacionService;

    @Autowired
    private SesionService sesionService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private AsesorService asesorService;

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
    
    @GetMapping("/asesores")
    public ResponseEntity<List<Asesor>> obtenerTodosLosAsesores() {
        List<Asesor> asesores = asesorService.obtenerTodosLosAsesores();
        return ResponseEntity.ok(asesores);
    }

    @GetMapping("/{idEstudiante}/sesiones")
    public ResponseEntity<List<Sesion>> obtenerSesionesProgramadas(@PathVariable Integer idEstudiante) {
        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        if (usuarioAutenticado.getIdUsuario().equals(idEstudiante)) {
            List<Sesion> sesiones = sesionService.obtenerSesionesPorUsuario(idEstudiante);
            return ResponseEntity.ok(sesiones);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado. Solo puedes ver tus propias sesiones.");
        }
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

    @PostMapping("/sesiones/{idSesion}/valoraciones")
    public ResponseEntity<Valoracion> valorarSesion(@PathVariable Integer idSesion, @RequestBody Valoracion valoracion) {
        valoracion.setUsuario(obtenerUsuarioAutenticado());
        Valoracion nuevaValoracion = valoracionService.guardarValoracion(valoracion);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaValoracion);
    }
    
    @GetMapping("/biblioteca/{idRecurso}")
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
    
    @PutMapping("/actualizar")
    public ResponseEntity<Usuario> actualizarPerfilEstudiante(@Validated @RequestBody ActualizarUsuarioRequest request, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        if (usuarioAutenticado == null) {
            throw new AuthenticationRequiredException("No estás autenticado como estudiante.");
        }
        usuarioAutenticado.setNombre(request.getNombre());
        usuarioAutenticado.setCorreoElectronico(request.getCorreoElectronico());
        Usuario usuarioActualizado = usuarioService.guardarUsuario(usuarioAutenticado); 

        return ResponseEntity.ok(usuarioActualizado);
    }
    
    @GetMapping("/perfil")
    public ResponseEntity<Usuario> obtenerDatosUsuarioAutenticado() {
        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        if (usuarioAutenticado != null) {
            return ResponseEntity.ok(usuarioAutenticado);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No estás autenticado.");
        }
    }

    public Usuario obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return usuarioService.buscarPorCorreoElectronico(authentication.getName());
        } else {
            return null;
        }
    } 
}

