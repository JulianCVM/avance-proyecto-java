package com.ai.avance.data.repositories;

import com.ai.avance.data.entities.ConversationEntities.ChatSessionEntity;
import com.ai.avance.data.entities.ConversationEntities.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para acceder a la entidad MessageEntity.
 */
@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    
    /**
     * Encuentra todos los mensajes de una sesión de chat.
     */
    List<MessageEntity> findByChatSession(ChatSessionEntity chatSession);
    
    /**
     * Encuentra todos los mensajes de una sesión de chat ordenados por timestamp.
     */
    List<MessageEntity> findByChatSessionOrderByTimestampAsc(ChatSessionEntity chatSession);
    
    /**
     * Elimina todos los mensajes de una sesión de chat.
     */
    void deleteByChatSession(ChatSessionEntity chatSession);
} 