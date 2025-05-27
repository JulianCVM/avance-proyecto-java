package com.ai.avance.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO para manejar solicitudes de usuario, incluyendo registro, login y gestión de APIs.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    // Datos básicos del usuario
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    
    // Campos para gestión de APIs
    @Builder.Default
    private List<ApiKeyDto> apiKeys = new ArrayList<>();
    private String currentApiKey;
    private String apiKeyName;
    private String apiKeyProvider; // "gemini", "openai", etc.
    
    // Campos para verificación y validación
    private String confirmPassword;
    private boolean rememberMe;
    private String verificationCode;
    
    // DTO anidado para gestionar las API keys del usuario
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiKeyDto {
        private Long id;
        private String name;
        private String provider;
        private String apiKey;
        private boolean isActive;
        private boolean isDefault;
    }
}
