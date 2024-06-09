package com.EduConnectB.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Permite todas las rutas
                        .allowedOrigins("http://localhost:5173") // Permite el origen de tu frontend
                        .allowedMethods("GET", "POST", "PUT", "DELETE") // Permite todos los métodos HTTP
                        .allowedHeaders("*") // Permite todos los headers
                		.allowCredentials(true);
            }
        };
    }
}