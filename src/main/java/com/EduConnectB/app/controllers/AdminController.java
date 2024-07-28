package com.EduConnectB.app.controllers;

import com.EduConnectB.app.models.Asesor;
import com.EduConnectB.app.models.EstadoUsuario;
import com.EduConnectB.app.models.Usuario;
import com.EduConnectB.app.services.ArchivoAsesorService;
import com.EduConnectB.app.services.AsesorService;
import com.EduConnectB.app.services.UsuarioService;
import com.itextpdf.text.DocumentException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController extends BaseController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AsesorService asesorService;
    
    @Autowired
    private ArchivoAsesorService archivoAsesorService;

    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> obtenerTodosLosUsuarios() {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authorities: " + auth.getAuthorities());
        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/usuarios/{idUsuario}")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable Integer idUsuario) {
        Usuario usuario = usuarioService.obtenerUsuarioPorId(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/usuarios")
    public ResponseEntity<Usuario> crearUsuario(@RequestBody Usuario usuario) {
        Usuario nuevoUsuario = usuarioService.guardarUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    @PutMapping("/usuarios/{idUsuario}")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Integer idUsuario, @RequestBody Usuario usuario) {
        Usuario usuarioActualizado = usuarioService.actualizarUsuario(idUsuario, usuario);
        return ResponseEntity.ok(usuarioActualizado);
    }

    @DeleteMapping("/usuarios/{idUsuario}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Integer idUsuario) {
        usuarioService.eliminarUsuario(idUsuario);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/asesores")
    public ResponseEntity<List<Asesor>> obtenerTodosLosAsesores() {
        List<Asesor> asesores = asesorService.obtenerTodosLosAsesores();
        return ResponseEntity.ok(asesores);
    }

    @GetMapping("/asesores/{idAsesor}")
    public ResponseEntity<Asesor> obtenerAsesorPorId(@PathVariable Integer idAsesor) {
        Asesor asesor = asesorService.obtenerAsesorPorId(idAsesor)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asesor no encontrado"));
        return ResponseEntity.ok(asesor);
    }

    @PostMapping("/asesores")
    public ResponseEntity<Asesor> crearAsesor(@RequestBody Asesor asesor) {
        Asesor nuevoAsesor = asesorService.guardarAsesor(asesor);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoAsesor);
    }
    
    @GetMapping("/asesores/pendientes")
    public ResponseEntity<List<Asesor>> obtenerAsesoresPendientesDeAprobacion() {
        List<Asesor> asesoresPendientes = asesorService.obtenerAsesoresPorEstado(EstadoUsuario.PENDIENTE_APROBACION);
        return ResponseEntity.ok(asesoresPendientes);
    }


    @PutMapping("/asesores/{idAsesor}")
    public ResponseEntity<Asesor> actualizarAsesor(@PathVariable Integer idAsesor, @RequestBody Asesor asesor) {
        Asesor asesorActualizado = asesorService.actualizarAsesor(idAsesor, asesor);
        return ResponseEntity.ok(asesorActualizado);
    }

    @DeleteMapping("/asesores/{idAsesor}")
    public ResponseEntity<Void> eliminarAsesor(@PathVariable Integer idAsesor) {
        asesorService.eliminarAsesor(idAsesor);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/asesores/{idAsesor}/aprobar")
    public ResponseEntity<Asesor> aprobarAsesor(@PathVariable Integer idAsesor) {
        Asesor asesor = asesorService.obtenerAsesorPorId(idAsesor)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asesor no encontrado"));

        if (asesor.getUsuario().getEstado() != EstadoUsuario.PENDIENTE_APROBACION) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El asesor no está pendiente de aprobación.");
        }

        asesorService.aprobarAsesor(asesor);
        return ResponseEntity.ok(asesor);
    }

    @PutMapping("/asesores/{idAsesor}/rechazar")
    public ResponseEntity<Void> rechazarAsesor(@PathVariable Integer idAsesor) {
        Asesor asesor = asesorService.obtenerAsesorPorId(idAsesor)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asesor no encontrado"));

        if (asesor.getUsuario().getEstado() != EstadoUsuario.PENDIENTE_APROBACION) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El asesor no está pendiente de aprobación.");
        }

        asesorService.rechazarAsesor(asesor);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/archivos/{archivoId}/descargar")
    public ResponseEntity<byte[]> descargarArchivo(@PathVariable Integer archivoId) throws IOException, DocumentException {
        byte[] pdfBytes = archivoAsesorService.descargarArchivo(archivoId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment",
     "Sustento.pdf");
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
