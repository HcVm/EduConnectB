package com.EduConnectB.app.controllers;

import com.EduConnectB.app.dto.CompraMembresiaRequest;
import com.EduConnectB.app.exceptions.AuthenticationRequiredException;
import com.EduConnectB.app.models.EstadoUsuario;
import com.EduConnectB.app.models.Membresia;
import com.EduConnectB.app.models.Usuario;
import com.EduConnectB.app.services.MembresiaService;
import com.EduConnectB.app.services.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
public class MembresiaController extends BaseController {

    @Autowired
    private MembresiaService membresiaService;

    @Autowired
    private UsuarioService usuarioService;
    
    // Comprar membresía (simulación de pago ya que no tenemos integrada una pasarela real amigos)
    @PostMapping("/comprar")
    public ResponseEntity<?> comprarMembresia(@Validated @RequestBody CompraMembresiaRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        Usuario usuario = usuarioService.findByTokenTemporal(request.getTokenTemporal())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado o token temporal inválido"));

        boolean pagoExitoso = true; 


        if (pagoExitoso) {
            Membresia nuevaMembresia = new Membresia();
            nuevaMembresia.setUsuario(usuario);
            nuevaMembresia.setTipoMembresia(request.getTipoMembresia());
            nuevaMembresia.setFechaInicio(LocalDate.now());
            nuevaMembresia.setFechaFin(LocalDate.now().plusMonths(1)); 
            membresiaService.guardarMembresia(nuevaMembresia);

            usuario.setTipoUsuario(request.getTipoMembresia().getTipoUsuarioAsociado());
            usuario.setEstado(EstadoUsuario.ACTIVO);
            usuario.setTokenTemporal(null);
            usuarioService.guardarUsuario(usuario);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", "/login"); 
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
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
    
    @PreAuthorize("hasAnyAuthority('ESTUDIANTE', 'ASESOR')")
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
