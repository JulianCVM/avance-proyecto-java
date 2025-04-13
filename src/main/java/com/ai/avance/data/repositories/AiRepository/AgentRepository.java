package com.ai.avance.data.repositories.AiRepository;

import com.ai.avance.data.entities.AIEntities.AgentEntity;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar las operaciones CRUD de agentes IA.
 */
public interface AgentRepository {
    
    /**
     * Guarda un nuevo agente o actualiza uno existente.
     */
    AgentEntity save(AgentEntity agent);
    
    /**
     * Busca un agente por su ID.
     */
    Optional<AgentEntity> findById(Long id);
    
    /**
     * Busca todos los agentes creados por un usuario.
     */
    List<AgentEntity> findByUserId(Long userId);
    
    /**
     * Busca agentes por nombre o descripción que coincidan con el término.
     */
    List<AgentEntity> findByNameOrDescriptionContains(String searchTerm);
    
    /**
     * Elimina un agente por su ID.
     */
    void deleteById(Long id);
    
    /**
     * Busca todos los agentes activos.
     */
    List<AgentEntity> findByActiveTrue();
} 