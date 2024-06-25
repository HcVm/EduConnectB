package com.EduConnectB.app.controllers;

import com.EduConnectB.app.dto.CompraMembresiaRequest;
import com.EduConnectB.app.exceptions.AuthenticationRequiredException;
import com.EduConnectB.app.models.EstadoUsuario;
import com.EduConnectB.app.models.Membresia;
import com.EduConnectB.app.models.Pago;
import com.EduConnectB.app.models.Usuario;
import com.EduConnectB.app.services.ComprobanteService;
import com.EduConnectB.app.services.MembresiaService;
import com.EduConnectB.app.services.PagoService;
import com.EduConnectB.app.services.UsuarioService;
import com.itextpdf.text.DocumentException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/membresias")
public class MembresiaController extends BaseController {

    @Autowired
    private MembresiaService membresiaService;

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private PagoService pagoService;
    
    @Autowired
    private ComprobanteService comprobanteService;
    
    // Comprar membresía (simulación de pago ya que no tenemos integrada una pasarela real amigos)
    @PostMapping("/comprar")
    public ResponseEntity<?> comprarMembresia(@Validated @RequestBody CompraMembresiaRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        Usuario usuario = usuarioService.findByTokenTemporal(request.getTokenTemporal())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado o token temporal inválido"));

        Pago pago = pagoService.procesarPago(usuario, request.getTipoMembresia(), request.getDatosPago());

        if (pago != null) { 
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en el pago. Verifica los datos de la tarjeta.");
        }
    }
    
    @PreAuthorize("hasAnyAuthority('ESTUDIANTE')")
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
    
    @PreAuthorize("hasAnyAuthority('ESTUDIANTE')")
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
    
    @PreAuthorize("hasAnyAuthority('ESTUDIANTE')")
    @GetMapping("/mi-membresia/comprobante")
    public ResponseEntity<byte[]> obtenerComprobanteMembresia() {
        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        if (usuarioAutenticado == null) {
            throw new AuthenticationRequiredException("No estás autenticado.");
        }

        Membresia membresia = membresiaService.obtenerMembresiaPorUsuario(usuarioAutenticado);
        if (membresia == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No tienes una membresía activa.");
        }

        Pago ultimoPago = pagoService.obtenerUltimoPago(membresia);

        if (ultimoPago == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró ningún pago para esta membresía.");
        }

        try {
            byte[] pdfBytes = comprobanteService.generarComprobantePDF(membresia, ultimoPago);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "comprobantePago.pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (DocumentException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al generar el comprobante.", e);
        }
    }
    
    @PreAuthorize("hasAnyAuthority('ESTUDIANTE')")
    @GetMapping("/mi-membresia/datos-comprobante")
    public ResponseEntity<Map<String, Object>> obtenerDatosComprobante() {
        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        if (usuarioAutenticado == null) {
            throw new AuthenticationRequiredException("No estás autenticado.");
        }

        Membresia membresia = membresiaService.obtenerMembresiaPorUsuario(usuarioAutenticado);
        if (membresia == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No tienes una membresía activa.");
        }

        Pago ultimoPago = pagoService.obtenerUltimoPago(membresia);
        if (ultimoPago == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró ningún pago para esta membresía.");
        }

        Map<String, Object> datosComprobante = new HashMap<>();
        datosComprobante.put("membresia", membresia);
        datosComprobante.put("pago", ultimoPago);
        return ResponseEntity.ok(datosComprobante);
    }
    
    @PreAuthorize("hasAnyAuthority('ESTUDIANTE')")
    @PostMapping("/renovar")
    public ResponseEntity<?> renovarMembresia(@Validated @RequestBody CompraMembresiaRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        if (usuarioAutenticado == null) {
            throw new AuthenticationRequiredException("No estás autenticado.");
        }
        Membresia membresia = membresiaService.obtenerMembresiaPorUsuario(usuarioAutenticado);
        if (membresia == null || membresia.getFechaFin().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No tienes una membresía activa para renovar.");
        }

        Pago pago = pagoService.procesarPago(usuarioAutenticado, membresia.getTipoMembresia(), request.getDatosPago());

        if (pago != null) {
            membresia.setFechaFin(membresia.getFechaFin().plusMonths(1));
            membresiaService.guardarMembresia(membresia);

            Map<String, Object> response = new HashMap<>();
            response.put("membresia", membresia);

            return ResponseEntity.ok(response); 
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en el pago. Verifica los datos de la tarjeta.");
        }
    }
    
    @PreAuthorize("hasAnyAuthority('ESTUDIANTE')")
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
