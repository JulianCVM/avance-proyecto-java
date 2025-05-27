package com.ai.avance.services;

import com.ai.avance.data.entities.TokenEntities.ApiTokenEntity;
import com.ai.avance.data.entities.UserEntities.UserEntity;
import com.ai.avance.data.repositories.ApiTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar integraciones con APIs externas.
 */
@Service
@RequiredArgsConstructor
public class IntegrationService {

    private final ApiTokenRepository apiTokenRepository;
    
    /**
     * Obtiene todos los tokens de API.
     */
    public List<ApiTokenEntity> getAllApiTokens() {
        return apiTokenRepository.findAll();
    }
    
    /**
     * Obtiene un token de API por su ID.
     */
    public Optional<ApiTokenEntity> getApiTokenById(Long id) {
        return apiTokenRepository.findById(id);
    }
    
    /**
     * Obtiene tokens de API para un proveedor específico.
     */
    public List<ApiTokenEntity> getApiTokensByProvider(String provider) {
        return apiTokenRepository.findByProvider(provider);
    }
    
    /**
     * Obtiene el token activo para un proveedor específico.
     */
    public List<ApiTokenEntity> getActiveTokensForProvider(String provider) {
        return apiTokenRepository.findByProviderAndIsActiveTrue(provider);
    }
    
    /**
     * Obtiene tokens de API para un usuario específico.
     */
    public List<ApiTokenEntity> getApiTokensForUser(UserEntity user) {
        return apiTokenRepository.findByUser(user);
    }
    
    /**
     * Busca tokens por nombre.
     */
    public List<ApiTokenEntity> getApiTokensByName(String name) {
        return apiTokenRepository.findByName(name);
    }
    
    /**
     * Busca tokens activos por nombre.
     */
    public List<ApiTokenEntity> getActiveApiTokensByName(String name) {
        return apiTokenRepository.findByNameAndIsActiveTrue(name);
    }
    
    /**
     * Crea un nuevo token de API.
     */
    @Transactional
    public ApiTokenEntity createApiToken(ApiTokenEntity token) {
        token.setCreatedAt(LocalDateTime.now());
        
        // Si este token está marcado como activo, desactivar cualquier otro token activo para este proveedor
        if (token.isActive()) {
            deactivateTokensForProvider(token.getProvider());
        }
        
        return apiTokenRepository.save(token);
    }
    
    /**
     * Actualiza un token de API existente.
     */
    @Transactional
    public Optional<ApiTokenEntity> updateApiToken(Long id, ApiTokenEntity updated) {
        return apiTokenRepository.findById(id).map(token -> {
            // Actualizar campos
            if (updated.getTokenValue() != null && !updated.getTokenValue().isEmpty()) {
                token.setTokenValue(updated.getTokenValue());
            }
            if (updated.getProvider() != null) {
                token.setProvider(updated.getProvider());
            }
            if (updated.getName() != null) {
                token.setName(updated.getName());
            }
            
            // Si este token está siendo activado, desactivar cualquier otro token activo para este proveedor
            if (updated.isActive() && !token.isActive()) {
                deactivateTokensForProvider(token.getProvider());
                token.setActive(true);
            } else if (!updated.isActive()) {
                token.setActive(false);
            }
            
            token.setLastUsed(LocalDateTime.now());
            
            return apiTokenRepository.save(token);
        });
    }
    
    /**
     * Elimina un token de API.
     */
    @Transactional
    public void deleteApiToken(Long id) {
        apiTokenRepository.deleteById(id);
    }
    
    /**
     * Desactiva todos los tokens para un proveedor específico.
     */
    @Transactional
    public void deactivateTokensForProvider(String provider) {
        List<ApiTokenEntity> activeTokens = apiTokenRepository.findByProviderAndIsActiveTrue(provider);
        
        for (ApiTokenEntity token : activeTokens) {
            token.setActive(false);
            token.setLastUsed(LocalDateTime.now());
            apiTokenRepository.save(token);
        }
    }
    
    /**
     * Verifica si un proveedor tiene un token activo.
     */
    public boolean hasActiveTokenForProvider(String provider) {
        return !apiTokenRepository.findByProviderAndIsActiveTrue(provider).isEmpty();
    }
    
    /**
     * Actualiza la última vez que se usó un token.
     */
    @Transactional
    public void updateLastUsed(Long tokenId) {
        apiTokenRepository.findById(tokenId).ifPresent(token -> {
            token.setLastUsed(LocalDateTime.now());
            apiTokenRepository.save(token);
        });
    }
}
