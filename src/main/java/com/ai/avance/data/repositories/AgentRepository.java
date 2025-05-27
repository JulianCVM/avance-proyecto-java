package com.ai.avance.data.repositories;

import com.ai.avance.data.entities.AIEntities.AgentEntity;
import com.ai.avance.data.entities.UserEntities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para acceder a la entidad AgentEntity.
 */
@Repository
public interface AgentRepository extends JpaRepository<AgentEntity, Long> {
    
    /**
     * Encuentra todos los agentes que pertenecen a un usuario específico.
     */
    List<AgentEntity> findByUser(UserEntity user);
    
    /**
     * Encuentra todos los agentes activos que pertenecen a un usuario específico.
     */
    List<AgentEntity> findByUserAndActiveTrue(UserEntity user);
    
    /**
     * Busca un agente por su ID y usuario.
     */
    Optional<AgentEntity> findByIdAndUser(Long id, UserEntity user);
    
    /**
     * Busca un agente por token API.
     */
    Optional<AgentEntity> findByApiToken(String apiToken);
    
    /**
     * Busca agentes por nombre.
     */
    List<AgentEntity> findByName(String name);
    
    /**
     * Busca agentes por nombre o descripción que contengan un término.
     */
    List<AgentEntity> findByNameContainingOrDescriptionContaining(String name, String description);
    
    /**
     * Encuentra todos los agentes activos.
     */
    List<AgentEntity> findByActiveTrue();
} 