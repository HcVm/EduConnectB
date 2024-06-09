package com.EduConnectB.app.config;

import com.EduConnectB.app.security.JwtAuthenticationFilter;
import com.EduConnectB.app.security.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import com.EduConnectB.app.dao.UsuarioRepository;
import com.EduConnectB.app.models.Usuario;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	@Lazy
	private JwtAuthenticationFilter jwtAuthenticationFilter;
	
    private final UsuarioRepository usuarioRepository;
    private final JwtConfig jwtConfig;
    private final CorsConfigurationSource corsConfigurationSource;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    public SecurityConfig(UsuarioRepository usuarioRepository, JwtConfig jwtConfig, CorsConfigurationSource corsConfigurationSource) {
        this.usuarioRepository = usuarioRepository;
        this.jwtConfig = jwtConfig;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/estudiantes/**").hasAnyAuthority("ESTUDIANTE_ESTANDAR", "ESTUDIANTE_PRO")
                .requestMatchers("/asesores/**").hasAuthority("ASESOR")
                .requestMatchers("/admin/**").hasAuthority("ADMIN")
                .requestMatchers("/error","/registro/**", "/login", "/usuarios/current").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterAfter(jwtAuthenticationFilter, AnonymousAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            Usuario usuario = usuarioRepository.findByCorreoElectronico(username);
            if (usuario == null) {
                throw new UsernameNotFoundException("Usuario no encontrado");
            }
            return new EduConnectUserDetails(usuario);
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
