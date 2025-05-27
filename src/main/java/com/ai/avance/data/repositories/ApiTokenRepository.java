package com.ai.avance.data.repositories;

import com.ai.avance.data.entities.TokenEntities.ApiTokenEntity;
import com.ai.avance.data.entities.UserEntities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para acceder a la entidad ApiTokenEntity.
 */
@Repository
public interface ApiTokenRepository extends JpaRepository<ApiTokenEntity, Long> {
    
    /**
     * Encuentra todos los tokens de API que pertenecen a un usuario específico.
     */
    List<ApiTokenEntity> findByUser(UserEntity user);
    
    /**
     * Encuentra todos los tokens de API para un proveedor específico.
     */
    List<ApiTokenEntity> findByProvider(String provider);
    
    /**
     * Encuentra todos los tokens de API activos para un proveedor específico.
     */
    List<ApiTokenEntity> findByProviderAndIsActiveTrue(String provider);
    
    /**
     * Encuentra un token de API activo para un usuario y proveedor específicos.
     */
    Optional<ApiTokenEntity> findByUserAndProviderAndIsActiveTrue(UserEntity user, String provider);
    
    /**
     * Encuentra todos los tokens de API con un nombre específico.
     */
    List<ApiTokenEntity> findByName(String name);
    
    /**
     * Encuentra todos los tokens de API activos con un nombre específico.
     */
    List<ApiTokenEntity> findByNameAndIsActiveTrue(String name);
} 