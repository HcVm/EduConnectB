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

import com.EduConnectB.app.dto.SolicitudSesionRequest;
import com.EduConnectB.app.exceptions.AuthenticationRequiredException;
import com.EduConnectB.app.models.Asesor;
import com.EduConnectB.app.models.EstadoSesion;
import com.EduConnectB.app.models.Sesion;
import com.EduConnectB.app.models.Usuario;
import com.EduConnectB.app.services.AsesorService;
import com.EduConnectB.app.services.MembresiaService;
import com.EduConnectB.app.services.SesionService;


@RestController
@RequestMapping("/sesiones")
public class SesionController extends BaseController {

    @Autowired
    private SesionService sesionService;

    @Autowired
    private AsesorService asesorService;
    
    @Autowired
    private MembresiaService membresiaService;
    
    @PostMapping("/solicitar")
    @PreAuthorize("hasAnyAuthority('ESTUDIANTE')")
    public ResponseEntity<Sesion> solicitarSesion(@RequestBody SolicitudSesionRequest request) {
        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        if (usuarioAutenticado == null) {
            throw new AuthenticationRequiredException("No estás autenticado.");
        }

        if (!membresiaService.tieneMembresiaActiva(usuarioAutenticado)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes una membresía activa para solicitar sesiones.");
        }

        Asesor asesor = asesorService.obtenerAsesorPorId(request.getIdAsesor())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asesor no encontrado."));

        if (!asesorService.estaDisponible(asesor, request.getFechaHora())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El asesor no está disponible en la fecha y hora solicitadas.");
        }

        Sesion nuevaSesion = new Sesion();
        nuevaSesion.setUsuario(usuarioAutenticado);
        nuevaSesion.setAsesor(asesorService.obtenerAsesorPorId(request.getIdAsesor())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asesor no encontrado.")));
        nuevaSesion.setFechaHora(request.getFechaHora());
        nuevaSesion.setEstado(EstadoSesion.SOLICITADA);

        Sesion sesionGuardada = sesionService.guardarSesion(nuevaSesion);
        return ResponseEntity.status(HttpStatus.CREATED).body(sesionGuardada);
    }


    @PutMapping("/{idSesion}/{accion}")
    @PreAuthorize("hasAuthority('ASESOR')")
    public ResponseEntity<Sesion> responderSolicitudSesion(
            @PathVariable Integer idSesion,
            @PathVariable String accion) {

        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        if (usuarioAutenticado == null) {
            throw new AuthenticationRequiredException("No estás autenticado.");
        }

        Sesion sesion = sesionService.obtenerSesionPorId(idSesion)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sesión no encontrada."));

        Asesor asesorAutenticado = asesorService.obtenerAsesorPorUsuario(usuarioAutenticado)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asesor no encontrado."));

        if (!sesion.getAsesor().equals(asesorAutenticado)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para responder a esta solicitud.");
        }

        if (accion.equals("aceptar")) {
            sesionService.aceptarSolicitudSesion(sesion);
        } else if (accion.equals("rechazar")) {
            sesionService.rechazarSolicitudSesion(sesion);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Acción no válida.");
        }

        return ResponseEntity.ok(sesion);
    }


    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Sesion>> obtenerTodasLasSesiones() {
        List<Sesion> sesiones = sesionService.obtenerTodasLasSesiones();
        return ResponseEntity.ok(sesiones);
    }

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

    @PutMapping("/{idSesion}")
    @PreAuthorize("hasAnyAuthority('ESTUDIANTE', 'ASESOR')")
    public ResponseEntity<Sesion> actualizarSesion(@PathVariable Integer idSesion,
            @Validated @RequestBody Sesion sesionActualizada, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            sesionActualizada.setErrores(bindingResult.getAllErrors());
            return ResponseEntity.badRequest().body(sesionActualizada);
        }

        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();

        return sesionService.obtenerSesionPorId(idSesion)
                .map(sesion -> {
                    if (tienePermisoParaSesion(sesion, usuarioAutenticado)) {
                        sesion.setFechaHora(sesionActualizada.getFechaHora());
                        sesion.setEstado(sesionActualizada.getEstado());
                        return ResponseEntity.ok(sesionService.guardarSesion(sesion));
                    } else {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para actualizar esta sesión.");
                    }
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sesión no encontrada."));
    }

    @DeleteMapping("/{idSesion}")
    @PreAuthorize("hasAnyAuthority('ESTUDIANTE', 'ASESOR')")
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
