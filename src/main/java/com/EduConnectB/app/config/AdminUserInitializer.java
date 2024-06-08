package com.EduConnectB.app.config;

import com.EduConnectB.app.models.EstadoUsuario;
import com.EduConnectB.app.models.TipoUsuario;
import com.EduConnectB.app.models.Usuario;
import com.EduConnectB.app.dao.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminUserInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (!usuarioRepository.existsByCorreoElectronico("admin@educonnect.com")) {
            Usuario admin = new Usuario();
            admin.setNombre("Administrador");
            admin.setCorreoElectronico("admin@educonnect.com");
            admin.setContrasena(passwordEncoder.encode("adminpassword"));
            admin.setTipoUsuario(TipoUsuario.ADMIN);
            admin.setEstado(EstadoUsuario.ACTIVO);
            usuarioRepository.save(admin);
        } 
    }
}
