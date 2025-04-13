package com.ai.avance.data.repositories;

import com.ai.avance.data.entities.ConversationEntity.ChatSessionEntity;
import com.ai.avance.data.entities.ConversationEntity.MessageEntity;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar las operaciones CRUD de las sesiones de chat.
 */
public interface ChatRepository {
    
    /**
     * Guarda una nueva sesión de chat o actualiza una existente.
     */
    ChatSessionEntity saveSession(ChatSessionEntity session);
    
    /**
     * Busca una sesión de chat por su ID.
     */
    Optional<ChatSessionEntity> findSessionById(Long id);
    
    /**
     * Busca todas las sesiones de chat de un usuario.
     */
    List<ChatSessionEntity> findSessionsByUserId(Long userId);
    
    /**
     * Busca todas las sesiones de chat asociadas a un agente.
     */
    List<ChatSessionEntity> findSessionsByAgentId(Long agentId);
    
    /**
     * Elimina una sesión de chat por su ID.
     */
    void deleteSessionById(Long id);
    
    /**
     * Guarda un nuevo mensaje en una sesión.
     */
    MessageEntity saveMessage(MessageEntity message);
    
    /**
     * Busca todos los mensajes de una sesión de chat.
     */
    List<MessageEntity> findMessagesByChatSessionId(Long chatSessionId);
    
    /**
     * Elimina todos los mensajes de una sesión de chat.
     */
    void deleteMessagesByChatSessionId(Long chatSessionId);
} 