package com.EduConnectB.app.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.EduConnectB.app.exceptions.AuthenticationRequiredException;
import com.EduConnectB.app.models.Asesor;
import com.EduConnectB.app.models.EstadoSesion;
import com.EduConnectB.app.models.Sesion;
import com.EduConnectB.app.models.TipoUsuario;
import com.EduConnectB.app.models.Usuario;
import com.EduConnectB.app.services.AsesorService;
import com.EduConnectB.app.services.SesionService;


@RestController
@RequestMapping("/sesiones")
public class SesionController extends BaseController {

    @Autowired
    private SesionService sesionService;

    @Autowired
    private AsesorService asesorService;

    // Crear una nueva sesión
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ESTUDIANTE', 'ASESOR')")
    public ResponseEntity<Sesion> crearSesion(@Validated @RequestBody Sesion sesion, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Manejo de errores de validación
            sesion.setErrores(bindingResult.getAllErrors()); // Agrega los errores a la sesión
            return ResponseEntity.badRequest().body(sesion); // Devuelve la sesión con los errores
        }

        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        if (usuarioAutenticado == null) {
            throw new AuthenticationRequiredException("Se requiere autenticación para acceder a este recurso.");
        }

        // Asignar el usuario o asesor autenticado a la sesión (según el rol)
        if (usuarioAutenticado.getTipoUsuario() == TipoUsuario.ESTUDIANTE) {
            sesion.setUsuario(usuarioAutenticado);
        } else if (usuarioAutenticado.getTipoUsuario() == TipoUsuario.ASESOR) {
            Asesor asesor = asesorService.obtenerAsesorPorUsuario(usuarioAutenticado)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asesor no encontrado.")); // Ahora funciona correctamente
            sesion.setAsesor(asesor);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para crear una sesión.");
        }

        Sesion nuevaSesion = sesionService.guardarSesion(sesion);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaSesion);
    }

    // Obtener todas las sesiones (solo para administradores)
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Sesion>> obtenerTodasLasSesiones() {
        List<Sesion> sesiones = sesionService.obtenerTodasLasSesiones();
        return ResponseEntity.ok(sesiones);
    }

    // Obtener una sesión por ID
    @GetMapping("/{idSesion}")
    @PreAuthorize("hasAnyAuthority('ESTUDIANTE', 'ASESOR', 'ADMIN')")
    public ResponseEntity<Sesion> obtenerSesionPorId(@PathVariable Integer idSesion) {
        Sesion sesion = sesionService.obtenerSesionPorId(idSesion)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sesión no encontrada"));

        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        if (!tienePermisoParaSesion(sesion, usuarioAutenticado)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para acceder a esta sesión.");
        }

        return ResponseEntity.ok(sesion);
    }

    // Actualizar una sesión existente
    @PutMapping("/{idSesion}")
    @PreAuthorize("hasAnyAuthority('ESTUDIANTE', 'ASESOR')")
    public ResponseEntity<Sesion> actualizarSesion(@PathVariable Integer idSesion,
            @Validated @RequestBody Sesion sesionActualizada, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Manejo de errores de validación
            sesionActualizada.setErrores(bindingResult.getAllErrors()); // Agrega los errores a la sesión
            return ResponseEntity.badRequest().body(sesionActualizada); // Devuelve la sesión con los errores
        }

        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();

        return sesionService.obtenerSesionPorId(idSesion)
                .map(sesion -> {
                    if (tienePermisoParaSesion(sesion, usuarioAutenticado)) {
                        // Actualizar los campos permitidos de la sesión
                        sesion.setFechaHora(sesionActualizada.getFechaHora());
                        sesion.setEstado(sesionActualizada.getEstado());
                        return ResponseEntity.ok(sesionService.guardarSesion(sesion));
                    } else {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para actualizar esta sesión.");
                    }
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sesión no encontrada."));
    }

    // Cancelar una sesión
    @DeleteMapping("/{idSesion}")
    @PreAuthorize("hasAnyAuthority('ESTUDIANTE', 'ASESOR')")
    public ResponseEntity<Void> cancelarSesion(@PathVariable Integer idSesion) {
        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        if (usuarioAutenticado == null) {
            throw new AuthenticationRequiredException("Se requiere autenticación para acceder a este recurso.");
        }

        sesionService.cancelarSesion(idSesion, usuarioAutenticado);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{idSesion}/url-jitsi")
    @PreAuthorize("hasAnyAuthority('ESTUDIANTE', 'ASESOR')")
    public ResponseEntity<String> obtenerUrlJitsi(@PathVariable Integer idSesion) {
        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        if (usuarioAutenticado == null) {
            throw new AuthenticationRequiredException("No estás autenticado.");
        }

        Optional<Sesion> sesionOptional = sesionService.obtenerSesionPorId(idSesion);
        if (sesionOptional.isPresent()) {
            Sesion sesion = sesionOptional.get();

            if (tienePermisoParaSesion(sesion, usuarioAutenticado)) {
                if (sesion.getEstado() == EstadoSesion.PROGRAMADA || sesion.getEstado() == EstadoSesion.EN_CURSO) {
                    return ResponseEntity.ok(sesion.getUrlJitsi());
                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La sesión no está programada o en curso.");
                }
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para acceder a esta sesión.");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sesión no encontrada.");
        }
    }
}
