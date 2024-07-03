package com.EduConnectB.app.controllers;

import com.EduConnectB.app.dto.ActualizarAsesorRequest;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

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
        if (usuarioAutenticado != null && usuarioAutenticado.getIdUsuario().equals(idAsesor)) {
            List<Sesion> sesiones = sesionService.obtenerSesionesPorAsesor(idAsesor);
            return ResponseEntity.ok(sesiones);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado. Solo puedes ver tus propias sesiones.");
        }
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
        if (usuarioAutenticado != null && usuarioAutenticado.getIdUsuario().equals(idAsesor)) {
            Asesor asesor = asesorService.obtenerAsesorPorId(idAsesor)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asesor no encontrado."));
            asesor.setHorarioDisponibilidad(nuevoHorario);
            asesorService.guardarAsesor(asesor);
            return ResponseEntity.ok(asesor);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado. Solo puedes actualizar tu propio horario.");
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
    public ResponseEntity<Calificacion> ingresarCalificacion(@PathVariable Integer idSesion, @Validated @RequestBody Calificacion calificacion, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(calificacion); 
        }

        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        if (usuarioAutenticado == null || usuarioAutenticado.getTipoUsuario() != TipoUsuario.ASESOR) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para ingresar calificaciones.");
        }

        Optional<Sesion> sesionOptional = sesionService.obtenerSesionPorId(idSesion);
        if (sesionOptional.isPresent()) {
            Sesion sesion = sesionOptional.get();
            if (sesion.getAsesor().getUsuario().equals(usuarioAutenticado)) {
                calificacion.setSesion(sesion); 
                Calificacion nuevaCalificacion = calificacionService.guardarCalificacion(calificacion);
                return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCalificacion);
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para ingresar calificaciones para esta sesión.");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sesión no encontrada.");
        }
    }

    @PostMapping("/estudiantes/{idEstudiante}/informes")
    public ResponseEntity<Informe> ingresarInforme(
            @PathVariable Integer idEstudiante,
            @Validated @RequestBody Informe informe,
            BindingResult bindingResult) {
        
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(informe);
        }

        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        if (usuarioAutenticado == null || usuarioAutenticado.getTipoUsuario() != TipoUsuario.ASESOR) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para ingresar informes.");
        }

        Usuario estudiante = usuarioService.obtenerUsuarioPorId(idEstudiante)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudiante no encontrado."));

        informe.setEstudiante(estudiante);
        Informe nuevoInforme = informeService.guardarInforme(informe);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoInforme);
    }
    
    @GetMapping("/estudiantes")
    public ResponseEntity<List<Usuario>> obtenerEstudiantes() {
        List<Usuario> estudiantes = usuarioService.buscarPorTipoUsuario(TipoUsuario.ESTUDIANTE);
        return ResponseEntity.ok(estudiantes);
    }
}
