package com.betojc.app.inventory.config;

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
                registry.addMapping("/**")  // Ruta para la que deseas permitir solicitudes CORS
                        .allowedOrigins("http://localhost:5173")  // Origen permitido
                        .allowedMethods("GET", "POST", "PUT", "DELETE");  // Métodos HTTP permitidos
            }
        };
    }
}
