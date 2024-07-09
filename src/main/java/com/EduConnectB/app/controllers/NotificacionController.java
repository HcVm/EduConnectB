package com.EduConnectB.app.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.EduConnectB.app.models.Usuario;
import com.EduConnectB.app.services.NotificacionService;
import com.EduConnectB.app.services.UsuarioService;

@RestController
@RequestMapping("/notiftopic")
public class NotificacionController {
	
	@Autowired
    private NotificacionService notificacionService;
	
	@Autowired
    private UsuarioService usuarioService;

    @GetMapping("/{idUsuario}/notificaciones")
    public ResponseEntity<List<String>> obtenerNotificaciones(@PathVariable Integer idUsuario) {
        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        if (usuarioAutenticado == null || !usuarioAutenticado.getIdUsuario().equals(idUsuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para ver estas notificaciones.");
        }

        List<String> notificaciones = notificacionService.obtenerNotificaciones(usuarioAutenticado);
        return ResponseEntity.ok(notificaciones);
    }

    @DeleteMapping("/{idUsuario}/notificaciones")
    public ResponseEntity<Void> marcarNotificacionesComoLeidas(@PathVariable Integer idUsuario) {
        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        if (usuarioAutenticado == null || !usuarioAutenticado.getIdUsuario().equals(idUsuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para marcar estas notificaciones.");
        }

        notificacionService.marcarNotificacionesComoLeidas(usuarioAutenticado);
        return ResponseEntity.noContent().build();
    }
    
    public Usuario obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return usuarioService.buscarPorCorreoElectronico(authentication.getName());
        } else {
            return null;
        }
    } 

}
	