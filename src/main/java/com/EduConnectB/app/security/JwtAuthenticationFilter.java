package com.EduConnectB.app.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.EduConnectB.app.config.EduConnectUserDetails;
import com.EduConnectB.app.config.JwtConfig;
import com.EduConnectB.app.models.Usuario;
import com.EduConnectB.app.dao.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final AuthenticationManager authenticationManager; // Inyectar el AuthenticationManager directamente

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
                                   UsuarioRepository usuarioRepository,
                                   JwtConfig jwtConfig) { // Constructor con los parámetros necesarios
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.jwtConfig = jwtConfig;
    }

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String header = request.getHeader("Authorization");

        log.debug("Request URI: {}", request.getRequestURI());

        if (header == null || !header.startsWith("Bearer ")) {
            log.debug("No token provided or invalid header format");
            chain.doFilter(request, response);
            return;
        }

        String token = header.replace("Bearer ", "");
        String username = null;

        try {
            username = JWT.require(Algorithm.HMAC512(jwtConfig.getSecretKey().getBytes()))
                    .build()
                    .verify(token)
                    .getSubject();
            log.debug("Extracted username from token: {}", username);
        } catch (JWTVerificationException e) {
            log.error("Error verifying JWT token: {}", e.getMessage());
            // Maneja el error adecuadamente (por ejemplo, devuelve una respuesta de error)
        }

        if (username != null) {
            Usuario usuario = usuarioRepository.findByCorreoElectronico(username);
            if (usuario != null) {
                EduConnectUserDetails userDetails = new EduConnectUserDetails(usuario);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("User authenticated: {}", username);
            } else {
                log.warn("User not found in database: {}", username);
            }
        }

        chain.doFilter(request, response);
    }
}
