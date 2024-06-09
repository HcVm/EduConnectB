package com.EduConnectB.app.controllers;

import com.EduConnectB.app.dto.CompraMembresiaRequest;
import com.EduConnectB.app.exceptions.AuthenticationRequiredException;
import com.EduConnectB.app.models.EstadoUsuario;
import com.EduConnectB.app.models.Membresia;
import com.EduConnectB.app.models.TipoMembresia;
import com.EduConnectB.app.models.TipoUsuario;
import com.EduConnectB.app.models.Usuario;
import com.EduConnectB.app.services.MembresiaService;
import com.EduConnectB.app.services.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/membresias")
@PreAuthorize("hasAnyAuthority('ESTUDIANTE', 'ASESOR')")
public class MembresiaController extends BaseController {

    @Autowired
    private MembresiaService membresiaService;

    @Autowired
    private UsuarioService usuarioService;

    // Comprar membresía (simulación de pago ya que no tenemos integrada una pasarela)
    @PostMapping("/comprar")
    public ResponseEntity<Membresia> comprarMembresia(@Validated @RequestBody CompraMembresiaRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new Membresia()); 
        }

        Usuario usuario = usuarioService.obtenerUsuarioPorId(request.getUsuarioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        if (membresiaService.tieneMembresiaActiva(usuario)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya tienes una membresía activa.");
        }

        boolean pagoExitoso = true; 

        if (pagoExitoso) {
            Membresia nuevaMembresia = new Membresia();
            nuevaMembresia.setUsuario(usuario);
            nuevaMembresia.setTipoMembresia(request.getTipoMembresia());
            nuevaMembresia.setFechaInicio(LocalDate.now());
            nuevaMembresia.setFechaFin(LocalDate.now().plusMonths(1)); // Ejemplo: 1 mes de membresía
            
            nuevaMembresia = membresiaService.guardarMembresia(nuevaMembresia);
            if (nuevaMembresia.getTipoMembresia() == TipoMembresia.ASESOR) {
                usuario.setTipoUsuario(TipoUsuario.ASESOR);
            } else if (nuevaMembresia.getTipoMembresia() == TipoMembresia.ESTUDIANTE_PRO) {
                usuario.setTipoUsuario(TipoUsuario.ESTUDIANTE);
            } else if (nuevaMembresia.getTipoMembresia() == TipoMembresia.ESTUDIANTE_ESTANDAR){
                usuario.setTipoUsuario(TipoUsuario.ESTUDIANTE);
            }
            
            usuario.setEstado(EstadoUsuario.ACTIVO);
            usuarioService.guardarUsuario(usuario);

            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaMembresia);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en el pago.");
        }
    }

    @GetMapping("/estado")
    public ResponseEntity<Boolean> tieneMembresiaActiva() {
        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        if (usuarioAutenticado != null) {
            boolean tieneMembresiaActiva = membresiaService.tieneMembresiaActiva(usuarioAutenticado);
            return ResponseEntity.ok(tieneMembresiaActiva);
        } else {
            throw new AuthenticationRequiredException("No estás autenticado.");
        }
    }

    @GetMapping("/mi-membresia")
    public ResponseEntity<Membresia> obtenerMiMembresia() {
        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        if (usuarioAutenticado != null) {
            Membresia membresia = membresiaService.obtenerMembresiaPorUsuario(usuarioAutenticado);
            if (membresia != null) {
                return ResponseEntity.ok(membresia);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No tienes una membresía activa.");
            }
        } else {
            throw new AuthenticationRequiredException("No estás autenticado.");
        }
    }


    // Renovar la membresía del usuario (renovación real)
    //@PostMapping("/renovar")
    //public ResponseEntity<Membresia> renovarMembresia() {
    //Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
    //if (usuarioAutenticado != null) {
    //if (!membresiaService.tieneMembresiaActiva(usuarioAutenticado)) {
    //throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No tienes una membresía activa para renovar.");
    //}

    //Membresia membresia = membresiaService.renovarMembresia(usuarioAutenticado, false);
    //return ResponseEntity.ok(membresia);
    //} else {
    //throw new AuthenticationRequiredException("No estás autenticado.");
    //}
    //}

    // Renovar la membresía del usuario (simulación de pago)
    @PostMapping("/renovar/prueba")
    public ResponseEntity<Membresia> renovarMembresiaPrueba() {
        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        if (usuarioAutenticado != null) {
            if (!membresiaService.tieneMembresiaActiva(usuarioAutenticado)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No tienes una membresía activa para renovar.");
            }

            Membresia membresia = membresiaService.renovarMembresia(usuarioAutenticado, true);
            return ResponseEntity.ok(membresia);
        } else {
            throw new AuthenticationRequiredException("No estás autenticado.");
        }
    }
    
    @DeleteMapping("/cancelar")
    public ResponseEntity<Void> cancelarMembresia() {
        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        if (usuarioAutenticado != null) {
            if (!membresiaService.tieneMembresiaActiva(usuarioAutenticado)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No tienes una membresía activa para cancelar.");
            }

            membresiaService.cancelarMembresia(usuarioAutenticado);
            return ResponseEntity.noContent().build();
        } else {
            throw new AuthenticationRequiredException("No estás autenticado.");
        }
    }
}
