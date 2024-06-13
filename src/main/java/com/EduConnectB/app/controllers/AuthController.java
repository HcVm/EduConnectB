package com.EduConnectB.app.controllers;

import com.EduConnectB.app.dto.LoginRequest;
import com.EduConnectB.app.models.Usuario;
import com.EduConnectB.app.security.JwtService;
import com.EduConnectB.app.services.EmailService;
import com.EduConnectB.app.services.UsuarioService;
import com.EduConnectB.app.dto.NuevaContrasenaRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/restablecer-contrasena")
    public ResponseEntity<?> solicitarRestablecimientoContrasena(@RequestParam String correoElectronico) {
        Usuario usuario = usuarioService.buscarPorCorreoElectronico(correoElectronico);
        if (usuario == null) {
            return ResponseEntity.badRequest().body("Correo electrónico no registrado.");
        }

        String tokenRestablecimiento = usuarioService.generarTokenRestablecimiento(usuario);
        emailService.enviarCorreoRestablecimientoContrasena(usuario, tokenRestablecimiento);
        return ResponseEntity.ok("Se ha enviado un correo electrónico para restablecer tu contraseña.");
    }

    @PostMapping("/restablecer-contrasena/{token}")
    public ResponseEntity<?> restablecerContrasena(@PathVariable String token, @RequestBody NuevaContrasenaRequest request) {
        Usuario usuario = usuarioService.findByTokenRestablecimiento(token);
        if (usuario == null) {
            return ResponseEntity.badRequest().body("Token de restablecimiento inválido o expirado.");
        }

        usuario.setContrasena(passwordEncoder.encode(request.getNuevaContrasena()));
        usuario.setTokenRestablecimiento(null);
        usuarioService.guardarUsuario(usuario);
        return ResponseEntity.ok("Contraseña restablecida exitosamente.");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getCorreoElectronico(), loginRequest.getContrasena())
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getCorreoElectronico());
            String token = jwtService.generarToken(authentication);

            return ResponseEntity.ok(token);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }
    }
}
