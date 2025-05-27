package com.ai.avance.data.repositories;

import com.ai.avance.data.entities.ConversationEntities.DefaultChatSessionEntity;
import com.ai.avance.data.entities.ConversationEntities.DefaultMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para acceder a la entidad DefaultMessageEntity.
 */
@Repository
public interface DefaultMessageRepository extends JpaRepository<DefaultMessageEntity, Long> {
    
    /**
     * Encuentra todos los mensajes de una sesión de chat por defecto.
     */
    List<DefaultMessageEntity> findByChatSession(DefaultChatSessionEntity chatSession);
    
    /**
     * Encuentra todos los mensajes de una sesión de chat por defecto ordenados por timestamp.
     */
    List<DefaultMessageEntity> findByChatSessionOrderByTimestampAsc(DefaultChatSessionEntity chatSession);
    
    /**
     * Elimina todos los mensajes de una sesión de chat por defecto.
     */
    void deleteByChatSession(DefaultChatSessionEntity chatSession);
} 