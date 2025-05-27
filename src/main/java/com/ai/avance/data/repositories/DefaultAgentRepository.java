package com.ai.avance.data.repositories;

import com.ai.avance.data.entities.AIEntities.DefaultAgentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para acceder a la entidad DefaultAgentEntity.
 */
@Repository
public interface DefaultAgentRepository extends JpaRepository<DefaultAgentEntity, Long> {
    
    /**
     * Busca el agente por defecto activo.
     */
    Optional<DefaultAgentEntity> findByActiveTrue();
} 