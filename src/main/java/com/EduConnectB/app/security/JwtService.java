package com.EduConnectB.app.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.EduConnectB.app.config.EduConnectUserDetails;
import com.EduConnectB.app.config.JwtConfig;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private final JwtConfig jwtConfig;

    public JwtService(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    public String generarToken(Authentication auth) {
        EduConnectUserDetails userDetails = (EduConnectUserDetails) auth.getPrincipal();
        String authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withClaim("authorities", authorities)
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtConfig.getExpirationMs()))
                .sign(Algorithm.HMAC512(jwtConfig.getSecretKey().getBytes()));
    }

    public String validateToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC512(jwtConfig.getSecretKey().getBytes()))
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getSubject();
        } catch (JWTVerificationException exception) {
            return null;
        }
    }
}
