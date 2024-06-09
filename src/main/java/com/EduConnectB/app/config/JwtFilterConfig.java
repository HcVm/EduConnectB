package com.EduConnectB.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;

import com.EduConnectB.app.dao.UsuarioRepository;
import com.EduConnectB.app.security.JwtAuthenticationFilter;

@Configuration
public class JwtFilterConfig {
	@Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        return new JwtAuthenticationFilter(authenticationManager, usuarioRepository, jwtConfig);
    }

}
