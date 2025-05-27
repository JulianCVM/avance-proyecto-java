package com.ai.avance.services;

import com.ai.avance.data.entities.TokenEntities.ApiTokenEntity;
import com.ai.avance.data.entities.UserEntities.UserEntity;
import com.ai.avance.data.repositories.ApiTokenRepository;
import com.ai.avance.presentation.dto.UserRequest.ApiKeyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar las API keys de los usuarios.
 */
@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final ApiTokenRepository apiTokenRepository;
    
    /**
     * Guarda una nueva API key para un usuario.
     */
    @Transactional
    public ApiTokenEntity saveApiKey(UserEntity user, String apiKey, String provider, String name) {
        // Desactivar la API key predeterminada anterior
        if (name != null && !name.isEmpty()) {
            resetDefaultApiKey(user, provider);
        }
        
        ApiTokenEntity apiToken = new ApiTokenEntity();
        apiToken.setUser(user);
        apiToken.setTokenValue(apiKey);
        apiToken.setProvider(provider);
        apiToken.setName(name);
        apiToken.setActive(true);
        apiToken.setCreatedAt(LocalDateTime.now());
        
        return apiTokenRepository.save(apiToken);
    }
    
    /**
     * Verifica si la API key es válida haciendo una solicitud de prueba.
     * Retorna true si la verificación es exitosa.
     */
    public boolean verifyApiKey(String apiKey, String provider) {
        // Implementar verificación según el proveedor
        if ("gemini".equalsIgnoreCase(provider)) {
            // Lógica para verificar API de Gemini
            try {
                // Realizar llamada de prueba a la API
                return true; // Si la llamada es exitosa
            } catch (Exception e) {
                return false;
            }
        } else if ("openai".equalsIgnoreCase(provider)) {
            // Lógica para verificar API de OpenAI
            try {
                // Realizar llamada de prueba a la API
                return true; // Si la llamada es exitosa
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }
    
    /**
     * Desactiva la API key anterior.
     */
    @Transactional
    private void resetDefaultApiKey(UserEntity user, String provider) {
        Optional<ApiTokenEntity> defaultToken = apiTokenRepository.findByUserAndProviderAndIsActiveTrue(user, provider);
        defaultToken.ifPresent(token -> {
            token.setActive(false);
            apiTokenRepository.save(token);
        });
    }
    
    /**
     * Obtiene todas las API keys de un usuario.
     */
    public List<ApiKeyDto> getUserApiKeys(UserEntity user) {
        List<ApiTokenEntity> apiTokens = apiTokenRepository.findByUser(user);
        return apiTokens.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene la API key predeterminada de un usuario para un proveedor específico.
     */
    public Optional<ApiTokenEntity> getDefaultApiKey(UserEntity user, String provider) {
        return apiTokenRepository.findByUserAndProviderAndIsActiveTrue(user, provider);
    }
    
    /**
     * Convierte una entidad ApiTokenEntity a DTO.
     */
    private ApiKeyDto convertToDto(ApiTokenEntity entity) {
        return ApiKeyDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .provider(entity.getProvider())
                .apiKey(maskApiKey(entity.getTokenValue()))
                .isActive(entity.isActive())
                .isDefault(entity.isActive()) // Usamos isActive como sustituto de isDefault
                .build();
    }
    
    /**
     * Enmascara la API key para mostrarla en la interfaz.
     */
    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() < 8) {
            return "••••••••";
        }
        // Mostrar solo primeros 4 y últimos 4 caracteres
        return apiKey.substring(0, 4) + "••••••••" + apiKey.substring(apiKey.length() - 4);
    }
    
    /**
     * Elimina una API key.
     */
    @Transactional
    public void deleteApiKey(Long id, UserEntity user) {
        apiTokenRepository.findById(id).ifPresent(token -> {
            if (token.getUser().getId().equals(user.getId())) {
                apiTokenRepository.delete(token);
            }
        });
    }
} 