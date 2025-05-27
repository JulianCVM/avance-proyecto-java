package com.ai.avance.data.repositories;

import com.ai.avance.data.entities.ConversationEntities.DefaultChatSessionEntity;
import com.ai.avance.data.entities.UserEntities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para acceder a la entidad DefaultChatSessionEntity.
 */
@Repository
public interface DefaultChatSessionRepository extends JpaRepository<DefaultChatSessionEntity, Long> {
    
    /**
     * Encuentra todas las sesiones de chat por defecto que pertenecen a un usuario específico.
     */
    List<DefaultChatSessionEntity> findByUser(UserEntity user);
    
    /**
     * Busca una sesión de chat por defecto por su clave de sesión.
     */
    Optional<DefaultChatSessionEntity> findBySessionKey(String sessionKey);
    
    /**
     * Busca una sesión de chat por defecto por su ID y usuario.
     */
    Optional<DefaultChatSessionEntity> findByIdAndUser(Long id, UserEntity user);
} 