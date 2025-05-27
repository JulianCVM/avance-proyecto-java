package com.ai.avance.data.repositories;

import com.ai.avance.data.entities.ConversationEntities.ChatSessionEntity;
import com.ai.avance.data.entities.ConversationEntities.MessageEntity;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para el repositorio de chat que permite gestionar sesiones y mensajes.
 * Esta interfaz es implementada por ChatRepositoryImpl.
 */
public interface ChatRepository {
    
    /**
     * Guarda o actualiza una sesión de chat.
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
     * Guarda un mensaje en una sesión de chat.
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