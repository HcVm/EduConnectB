package com.EduConnectB.app.controllers;

import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.util.StringUtils;

import com.EduConnectB.app.models.ArchivoAsesor;
import com.EduConnectB.app.models.Asesor;
import com.EduConnectB.app.models.EstadoUsuario;
import com.EduConnectB.app.models.TipoUsuario;
import com.EduConnectB.app.models.Usuario;
import com.EduConnectB.app.services.AsesorService;
import com.EduConnectB.app.services.EmailService;
import com.EduConnectB.app.services.UsuarioService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    private EmailService emailService;
    
    @Autowired
    private Cloudinary cloudinary;
    
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
        emailService.enviarCorreoConfirmacionEstudiante(nuevoUsuario);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Registro exitoso. Se ha enviado un correo de confirmación.");
        response.put("usuarioId", nuevoUsuario.getIdUsuario());
        response.put("usuario", nuevoUsuario);
        response.put("tokenTemporal", tokenTemporal);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/comprarMembresia");
        return new ResponseEntity<>(response, headers, HttpStatus.CREATED);
    }

    @SuppressWarnings("null")
	@PostMapping(value = "/asesor", consumes = {"multipart/form-data"})
    public ResponseEntity<?> registrarAsesor(
            @RequestParam("asesor") String asesorJson,
            @RequestParam("archivo") MultipartFile archivo) {
    	
        Asesor asesor;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            asesor = objectMapper.readValue(asesorJson, Asesor.class);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error al parsear el JSON de asesor.");
        }

        if (!EMAIL_PATTERN.matcher(asesor.getUsuario().getCorreoElectronico()).matches()) {
            return ResponseEntity.badRequest().body("El correo electrónico no es válido.");
        }

        if (asesor.getUsuario().getContrasena().length() < MIN_PASSWORD_LENGTH) {
            return ResponseEntity.badRequest().body("La contraseña debe tener al menos 8 caracteres.");
        }

        if (usuarioService.existsByCorreoElectronico(asesor.getUsuario().getCorreoElectronico())) {
            return ResponseEntity.badRequest().body("El correo electrónico ya está registrado.");
        }

        asesor.getUsuario().setContrasena(passwordEncoder.encode(asesor.getUsuario().getContrasena()));
        asesor.getUsuario().setTipoUsuario(TipoUsuario.ASESOR);
        asesor.getUsuario().setEstado(EstadoUsuario.PENDIENTE_APROBACION);

        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(archivo.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
            String url = (String) uploadResult.get("url");

            ArchivoAsesor archivoAsesor = new ArchivoAsesor();
            archivoAsesor.setNombreArchivo(StringUtils.cleanPath(archivo.getOriginalFilename()));
            archivoAsesor.setRutaArchivo(url);
            archivoAsesor.setFechaSubida(LocalDateTime.now());
            archivoAsesor.setAsesor(asesor);

            asesor.getArchivos().add(archivoAsesor);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al subir el archivo a Cloudinary.", e);
        }

        Asesor nuevoAsesor = asesorService.guardarAsesor(asesor);
        emailService.enviarCorreoConfirmacionAsesor(nuevoAsesor);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Solicitud de registro enviada. Esperando aprobación del administrador.");
        response.put("asesorId", nuevoAsesor.getIdAsesor());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
