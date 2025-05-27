package com.ai.avance.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Clase que maneja las excepciones de autenticación cuando no se puede autenticar un usuario.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        // Envía un código de error 401 (No autorizado)
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, 
                "No autorizado: " + authException.getMessage());
    }
} 