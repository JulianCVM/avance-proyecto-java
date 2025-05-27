package com.ai.avance.services;

import com.ai.avance.data.entities.ConversationEntities.ChatSessionEntity;
import com.ai.avance.data.entities.ConversationEntities.MessageEntity;
import com.ai.avance.data.entities.UserEntities.UserEntity;
import com.ai.avance.data.entities.AIEntities.AgentEntity;
import com.ai.avance.data.repositories.ChatSessionRepository;
import com.ai.avance.data.repositories.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar las operaciones de chat.
 */
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatSessionRepository chatSessionRepository;
    private final MessageRepository messageRepository;
    
    /**
     * Guarda una sesión de chat.
     */
    public ChatSessionEntity saveSession(ChatSessionEntity session) {
        return chatSessionRepository.save(session);
    }
    
    /**
     * Busca una sesión de chat por su ID.
     */
    public Optional<ChatSessionEntity> findSessionById(Long id) {
        return chatSessionRepository.findById(id);
    }
    
    /**
     * Busca todas las sesiones de chat de un usuario.
     */
    public List<ChatSessionEntity> findSessionsByUser(UserEntity user) {
        return chatSessionRepository.findByUser(user);
    }
    
    /**
     * Busca todas las sesiones de chat asociadas a un agente.
     */
    public List<ChatSessionEntity> findSessionsByAgent(AgentEntity agent) {
        return chatSessionRepository.findByAgent(agent);
    }
    
    /**
     * Elimina una sesión de chat por su ID.
     */
    @Transactional
    public void deleteSessionById(Long id) {
        Optional<ChatSessionEntity> sessionOpt = chatSessionRepository.findById(id);
        
        sessionOpt.ifPresent(session -> {
            // Primero eliminar todos los mensajes
            messageRepository.deleteByChatSession(session);
            // Luego eliminar la sesión
            chatSessionRepository.delete(session);
        });
    }
    
    /**
     * Guarda un nuevo mensaje en una sesión.
     */
    public MessageEntity saveMessage(MessageEntity message) {
        return messageRepository.save(message);
    }
    
    /**
     * Busca todos los mensajes de una sesión de chat.
     */
    public List<MessageEntity> findMessagesByChatSession(ChatSessionEntity chatSession) {
        return messageRepository.findByChatSessionOrderByTimestampAsc(chatSession);
    }
    
    /**
     * Busca todos los mensajes de una sesión de chat por su ID.
     */
    public List<MessageEntity> findMessagesByChatSessionId(Long chatSessionId) {
        Optional<ChatSessionEntity> sessionOpt = chatSessionRepository.findById(chatSessionId);
        
        return sessionOpt
                .map(session -> messageRepository.findByChatSessionOrderByTimestampAsc(session))
                .orElse(List.of());
    }
    
    /**
     * Elimina todos los mensajes de una sesión de chat.
     */
    @Transactional
    public void deleteMessagesByChatSessionId(Long chatSessionId) {
        Optional<ChatSessionEntity> sessionOpt = chatSessionRepository.findById(chatSessionId);
        
        sessionOpt.ifPresent(session -> 
            messageRepository.deleteByChatSession(session)
        );
    }
} 