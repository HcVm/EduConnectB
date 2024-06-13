package com.EduConnectB.app.security;

import com.EduConnectB.app.config.EduConnectUserDetails;
import com.EduConnectB.app.models.Usuario;
import com.EduConnectB.app.dao.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Component
public class TemporalTokenAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
    private UsuarioRepository usuarioRepository;

    private static final Logger log = LoggerFactory.getLogger(TemporalTokenAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        log.info("Request URI: {}", request.getRequestURI());

        String token = request.getHeader("TokenTemporal");

        if (token != null && request.getServletPath().equals("/membresias/comprar")) {
            log.info("Token temporal encontrado: {}", token);

            Usuario usuario = usuarioRepository.findByTokenTemporal(token).orElse(null);

            if (usuario != null) {
                log.info("Usuario encontrado: {}", usuario.getCorreoElectronico());
                EduConnectUserDetails userDetails = new EduConnectUserDetails(usuario);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Usuario autenticado con token temporal: {}", usuario.getCorreoElectronico());
            } else {
                log.warn("Usuario no encontrado para el token temporal: {}", token);
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token temporal inválido.");
            }
        } 

        filterChain.doFilter(request, response);
    }
}
