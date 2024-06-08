package com.EduConnectB.app.controllers;

import com.EduConnectB.app.exceptions.AuthenticationRequiredException;
import com.EduConnectB.app.models.EstadoUsuario;
import com.EduConnectB.app.models.Membresia;
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

    // Comprar membresía (simulación de pago)
    @PostMapping("/comprar/prueba")
    public ResponseEntity<Membresia> comprarMembresiaPrueba(@Validated @RequestBody Membresia membresia, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(membresia); // Devuelve la membresía con los errores
        }

        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        if (usuarioAutenticado == null) {
            throw new AuthenticationRequiredException("No estás autenticado.");
        }

        // Verificar si el usuario ya tiene una membresía activa (opcional)
        if (membresiaService.tieneMembresiaActiva(usuarioAutenticado)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya tienes una membresía activa.");
        }

        membresia.setUsuario(usuarioAutenticado);
        membresia.setFechaInicio(LocalDate.now());
        membresia.setFechaFin(LocalDate.now().plusMonths(1)); // Ejemplo: 1 mes de membresía

        // Actualizar el tipo de usuario según la membresía comprada
        usuarioAutenticado.setTipoUsuario(membresia.getTipoMembresia().getTipoUsuarioAsociado());
        usuarioAutenticado.setEstado(EstadoUsuario.ACTIVO);
        usuarioService.guardarUsuario(usuarioAutenticado);

        Membresia nuevaMembresia = membresiaService.guardarMembresia(membresia);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaMembresia);
    }

    // Verificar si el usuario tiene una membresía activa
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

    // Obtener detalles de la membresía del usuario
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
    @PostMapping("/renovar")
    public ResponseEntity<Membresia> renovarMembresia() {
        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        if (usuarioAutenticado != null) {
            // Verificar si el usuario tiene una membresía activa para renovar
            if (!membresiaService.tieneMembresiaActiva(usuarioAutenticado)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No tienes una membresía activa para renovar.");
            }

            Membresia membresia = membresiaService.renovarMembresia(usuarioAutenticado, false);
            return ResponseEntity.ok(membresia);
        } else {
            throw new AuthenticationRequiredException("No estás autenticado.");
        }
    }

    // Renovar la membresía del usuario (simulación de pago)
    @PostMapping("/renovar/prueba")
    public ResponseEntity<Membresia> renovarMembresiaPrueba() {
        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        if (usuarioAutenticado != null) {
            // Verificar si el usuario tiene una membresía activa para renovar (incluso en modo de prueba)
            if (!membresiaService.tieneMembresiaActiva(usuarioAutenticado)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No tienes una membresía activa para renovar.");
            }

            Membresia membresia = membresiaService.renovarMembresia(usuarioAutenticado, true);
            return ResponseEntity.ok(membresia);
        } else {
            throw new AuthenticationRequiredException("No estás autenticado.");
        }
    }
    
    // Cancelar la membresía del usuario
    @DeleteMapping("/cancelar")
    public ResponseEntity<Void> cancelarMembresia() {
        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        if (usuarioAutenticado != null) {
            // Verificar si el usuario tiene una membresía activa para cancelar
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
