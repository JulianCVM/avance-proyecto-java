package com.ai.avance.data.repositories;

import com.ai.avance.data.entities.AIEntities.AgentEntity;
import com.ai.avance.data.entities.ConversationEntities.ChatSessionEntity;
import com.ai.avance.data.entities.UserEntities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para acceder a la entidad ChatSessionEntity.
 */
@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSessionEntity, Long> {
    
    /**
     * Encuentra todas las sesiones de chat que pertenecen a un usuario específico.
     */
    List<ChatSessionEntity> findByUser(UserEntity user);
    
    /**
     * Encuentra todas las sesiones de chat activas que pertenecen a un usuario específico.
     */
    List<ChatSessionEntity> findByUserAndActiveTrue(UserEntity user);
    
    /**
     * Encuentra todas las sesiones de chat para un agente específico.
     */
    List<ChatSessionEntity> findByAgent(AgentEntity agent);
    
    /**
     * Busca una sesión de chat por su ID y usuario.
     */
    Optional<ChatSessionEntity> findByIdAndUser(Long id, UserEntity user);
} 