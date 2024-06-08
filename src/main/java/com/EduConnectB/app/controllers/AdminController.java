package com.EduConnectB.app.controllers;

import com.EduConnectB.app.models.Asesor;
import com.EduConnectB.app.models.Usuario;
import com.EduConnectB.app.services.AsesorService;
import com.EduConnectB.app.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController extends BaseController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AsesorService asesorService;

    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> obtenerTodosLosUsuarios() {
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
}
