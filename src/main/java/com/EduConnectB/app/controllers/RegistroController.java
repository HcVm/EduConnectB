package com.EduConnectB.app.controllers;

import com.EduConnectB.app.models.Asesor;
import com.EduConnectB.app.models.EstadoUsuario;
import com.EduConnectB.app.models.TipoUsuario;
import com.EduConnectB.app.models.Usuario;
import com.EduConnectB.app.services.AsesorService;
import com.EduConnectB.app.services.NotificacionService;
import com.EduConnectB.app.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/registro")
public class RegistroController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AsesorService asesorService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private NotificacionService notificationService;
    

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final int MIN_PASSWORD_LENGTH = 8;

    @PostMapping("/estudiante")
    public ResponseEntity<?> registrarEstudiante(@Validated @RequestBody Usuario usuario, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        if (!EMAIL_PATTERN.matcher(usuario.getCorreoElectronico()).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo electrónico no es válido.");
        }

        if (usuario.getContrasena().length() < MIN_PASSWORD_LENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe tener al menos 8 caracteres.");
        }

        if (usuarioService.existsByCorreoElectronico(usuario.getCorreoElectronico())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo electrónico ya está registrado.");
        }
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        usuario.setTipoUsuario(TipoUsuario.ESTUDIANTE);
        usuario.setEstado(EstadoUsuario.PENDIENTE_PAGO);
        
        String tokenTemporal = UUID.randomUUID().toString();
        usuario.setTokenTemporal(tokenTemporal);

        Usuario nuevoUsuario = usuarioService.guardarUsuario(usuario);

        Map<String, Object> response = new HashMap<>();
        response.put("usuario", nuevoUsuario);
        response.put("tokenTemporal", tokenTemporal);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/comprarMembresia");
        return new ResponseEntity<>(response, headers, HttpStatus.CREATED);
    }

    @PostMapping("/asesor")
    public ResponseEntity<?> registrarAsesor(@Validated @RequestBody Asesor asesor, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        if (!EMAIL_PATTERN.matcher(asesor.getUsuario().getCorreoElectronico()).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo electrónico no es válido.");
        }

        if (asesor.getUsuario().getContrasena().length() < MIN_PASSWORD_LENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe tener al menos 8 caracteres.");
        }

        if (usuarioService.existsByCorreoElectronico(asesor.getUsuario().getCorreoElectronico())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo electrónico ya está registrado.");
        }
        asesor.getUsuario().setContrasena(passwordEncoder.encode(asesor.getUsuario().getContrasena()));
        asesor.getUsuario().setTipoUsuario(TipoUsuario.ASESOR);
        asesor.getUsuario().setEstado(EstadoUsuario.PENDIENTE_APROBACION);


        Asesor nuevoAsesor = asesorService.guardarAsesor(asesor);
        notificationService.enviarNotificacionNuevoAsesor(nuevoAsesor);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Solicitud de registro enviada. Esperando aprobación del administrador.");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
