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
@PreAuthorize("hasRole('ADMIN')") // Solo administradores autenticados pueden acceder
public class AdminController extends BaseController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AsesorService asesorService;

    // Obtener todos los usuarios
    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> obtenerTodosLosUsuarios() {
        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    // Obtener un usuario por ID
    @GetMapping("/usuarios/{idUsuario}")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable Integer idUsuario) {
        Usuario usuario = usuarioService.obtenerUsuarioPorId(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return ResponseEntity.ok(usuario);
    }

    // Crear un nuevo usuario (puede ser estudiante, asesor o administrador)
    @PostMapping("/usuarios")
    public ResponseEntity<Usuario> crearUsuario(@RequestBody Usuario usuario) {
        // Validar los datos del usuario antes de guardarlo
        Usuario nuevoUsuario = usuarioService.guardarUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    // Actualizar un usuario existente
    @PutMapping("/usuarios/{idUsuario}")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Integer idUsuario, @RequestBody Usuario usuario) {
        // Validar los datos del usuario y verificar si existe
        Usuario usuarioActualizado = usuarioService.actualizarUsuario(idUsuario, usuario);
        return ResponseEntity.ok(usuarioActualizado);
    }

    // Eliminar un usuario
    @DeleteMapping("/usuarios/{idUsuario}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Integer idUsuario) {
        usuarioService.eliminarUsuario(idUsuario);
        return ResponseEntity.noContent().build();
    }

    // Obtener todos los asesores
    @GetMapping("/asesores")
    public ResponseEntity<List<Asesor>> obtenerTodosLosAsesores() {
        List<Asesor> asesores = asesorService.obtenerTodosLosAsesores();
        return ResponseEntity.ok(asesores);
    }

    // Obtener un asesor por ID
    @GetMapping("/asesores/{idAsesor}")
    public ResponseEntity<Asesor> obtenerAsesorPorId(@PathVariable Integer idAsesor) {
        Asesor asesor = asesorService.obtenerAsesorPorId(idAsesor)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asesor no encontrado"));
        return ResponseEntity.ok(asesor);
    }

    // Crear un nuevo asesor
    @PostMapping("/asesores")
    public ResponseEntity<Asesor> crearAsesor(@RequestBody Asesor asesor) {
        // Validar los datos del asesor antes de guardarlo
        Asesor nuevoAsesor = asesorService.guardarAsesor(asesor);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoAsesor);
    }

    // Actualizar un asesor existente
    @PutMapping("/asesores/{idAsesor}")
    public ResponseEntity<Asesor> actualizarAsesor(@PathVariable Integer idAsesor, @RequestBody Asesor asesor) {
        // Validar los datos del asesor y verificar si existe
        Asesor asesorActualizado = asesorService.actualizarAsesor(idAsesor, asesor);
        return ResponseEntity.ok(asesorActualizado);
    }

    // Eliminar un asesor
    @DeleteMapping("/asesores/{idAsesor}")
    public ResponseEntity<Void> eliminarAsesor(@PathVariable Integer idAsesor) {
        asesorService.eliminarAsesor(idAsesor);
        return ResponseEntity.noContent().build();
    }
}
