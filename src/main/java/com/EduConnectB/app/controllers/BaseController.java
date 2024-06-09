package com.EduConnectB.app.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EduConnectB.app.config.EduConnectUserDetails;
import com.EduConnectB.app.exceptions.AuthenticationRequiredException;
import com.EduConnectB.app.models.Sesion;
import com.EduConnectB.app.models.TipoUsuario;
import com.EduConnectB.app.models.Usuario;

@RestController
public abstract class BaseController {

	@ModelAttribute("usuarioAutenticado")
    public Usuario obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof EduConnectUserDetails) {
                return ((EduConnectUserDetails) principal).getUsuario();
            }
        }
        throw new AuthenticationRequiredException("Se requiere autenticación para acceder a este recurso.");
    }
	
	@GetMapping("/current")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Usuario> obtenerUsuarioActual() {
        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        return ResponseEntity.ok(usuarioAutenticado);
    }
    
    protected boolean tienePermisoParaSesion(Sesion sesion, Usuario usuarioAutenticado) {
        return sesion.getUsuario().equals(usuarioAutenticado) || 
               sesion.getAsesor().getUsuario().equals(usuarioAutenticado) ||
               usuarioAutenticado.getTipoUsuario() == TipoUsuario.ADMIN;
    }
}
