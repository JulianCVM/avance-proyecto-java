package com.ai.avance.data.repositories;

import com.ai.avance.data.entities.AIEntities.AgentEntity;
import com.ai.avance.data.entities.ConversationEntities.ChatSessionEntity;
import com.ai.avance.data.entities.ConversationEntities.MessageEntity;
import com.ai.avance.data.entities.UserEntities.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Implementación en memoria del repositorio de chat para desarrollo.
 * Esta implementación mantiene los datos en memoria y simula la funcionalidad de un repositorio JPA.
 */
@Repository
public class ChatRepositoryImpl implements ChatRepository {

    private final Map<Long, ChatSessionEntity> sessions = new HashMap<>();
    private final Map<Long, MessageEntity> messages = new HashMap<>();
    private final AtomicLong sessionIdCounter = new AtomicLong(1);
    private final AtomicLong messageIdCounter = new AtomicLong(1);

    @Override
    public ChatSessionEntity saveSession(ChatSessionEntity session) {
        if (session.getId() == null) {
            session.setId(sessionIdCounter.getAndIncrement());
        }
        sessions.put(session.getId(), deepCopySession(session));
        return sessions.get(session.getId());
    }

    @Override
    public Optional<ChatSessionEntity> findSessionById(Long id) {
        ChatSessionEntity session = sessions.get(id);
        if (session == null) {
            return Optional.empty();
        }
        return Optional.of(deepCopySession(session));
    }

    @Override
    public List<ChatSessionEntity> findSessionsByUserId(Long userId) {
        return sessions.values().stream()
                .filter(session -> session.getUser() != null && session.getUser().getId().equals(userId))
                .map(this::deepCopySession)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatSessionEntity> findSessionsByAgentId(Long agentId) {
        return sessions.values().stream()
                .filter(session -> session.getAgent() != null && session.getAgent().getId().equals(agentId))
                .map(this::deepCopySession)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteSessionById(Long id) {
        sessions.remove(id);
        // También eliminamos mensajes asociados a esta sesión
        deleteMessagesByChatSessionId(id);
    }

    @Override
    public MessageEntity saveMessage(MessageEntity message) {
        if (message.getId() == null) {
            message.setId(messageIdCounter.getAndIncrement());
        }
        
        // Asegurar que el mensaje tiene una referencia a la sesión
        if (message.getChatSession() != null && message.getChatSession().getId() != null) {
            // Buscar la sesión referenciada y añadir el mensaje a su lista
            ChatSessionEntity session = sessions.get(message.getChatSession().getId());
            if (session != null) {
                // Crear una copia profunda del mensaje antes de guardarlo
                MessageEntity messageCopy = deepCopyMessage(message);
                messages.put(messageCopy.getId(), messageCopy);
                
                // Actualizar la lista de mensajes en la sesión
                session.getMessages().add(messageCopy);
                return messageCopy;
            }
        }
        
        // Si llegamos aquí, guardar el mensaje sin asociarlo a una sesión
        MessageEntity messageCopy = deepCopyMessage(message);
        messages.put(messageCopy.getId(), messageCopy);
        return messageCopy;
    }

    @Override
    public List<MessageEntity> findMessagesByChatSessionId(Long chatSessionId) {
        return messages.values().stream()
                .filter(message -> message.getChatSession() != null && 
                        message.getChatSession().getId().equals(chatSessionId))
                .map(this::deepCopyMessage)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteMessagesByChatSessionId(Long chatSessionId) {
        // Encontrar los IDs de los mensajes que pertenecen a esta sesión
        List<Long> messageIdsToDelete = messages.values().stream()
                .filter(message -> message.getChatSession() != null && 
                        message.getChatSession().getId().equals(chatSessionId))
                .map(MessageEntity::getId)
                .collect(Collectors.toList());
        
        // Eliminar cada mensaje
        messageIdsToDelete.forEach(messages::remove);
        
        // Limpiar la lista de mensajes en la sesión si existe
        ChatSessionEntity session = sessions.get(chatSessionId);
        if (session != null) {
            session.setMessages(new ArrayList<>());
        }
    }
    
    /**
     * Crea una copia profunda de una sesión de chat para evitar modificaciones accidentales.
     */
    private ChatSessionEntity deepCopySession(ChatSessionEntity original) {
        if (original == null) {
            return null;
        }
        
        ChatSessionEntity copy = new ChatSessionEntity();
        copy.setId(original.getId());
        
        // Copiar usuario y agente (sin copiar sus relaciones internas)
        if (original.getUser() != null) {
            UserEntity userCopy = new UserEntity();
            userCopy.setId(original.getUser().getId());
            userCopy.setUsername(original.getUser().getUsername());
            copy.setUser(userCopy);
        }
        
        if (original.getAgent() != null) {
            AgentEntity agentCopy = new AgentEntity();
            agentCopy.setId(original.getAgent().getId());
            agentCopy.setName(original.getAgent().getName());
            copy.setAgent(agentCopy);
        }
        
        copy.setTitle(original.getTitle());
        copy.setCreatedAt(original.getCreatedAt());
        copy.setLastActivity(original.getLastActivity());
        copy.setActive(original.isActive());
        copy.setContextInfo(original.getContextInfo());
        copy.setTotalTokensUsed(original.getTotalTokensUsed());
        
        // No copiamos mensajes aquí para evitar referencia circular
        copy.setMessages(new ArrayList<>());
        
        return copy;
    }
    
    /**
     * Crea una copia profunda de un mensaje para evitar modificaciones accidentales.
     */
    private MessageEntity deepCopyMessage(MessageEntity original) {
        if (original == null) {
            return null;
        }
        
        MessageEntity copy = new MessageEntity();
        copy.setId(original.getId());
        
        // Crear una referencia simplificada a la sesión para evitar ciclos
        if (original.getChatSession() != null) {
            ChatSessionEntity sessionRef = new ChatSessionEntity();
            sessionRef.setId(original.getChatSession().getId());
            copy.setChatSession(sessionRef);
        }
        
        copy.setContent(original.getContent());
        copy.setRole(original.getRole());
        copy.setTimestamp(original.getTimestamp());
        copy.setTokenCount(original.getTokenCount());
        copy.setFlagged(original.isFlagged());
        copy.setFlagReason(original.getFlagReason());
        copy.setModelUsed(original.getModelUsed());
        
        return copy;
    }
} 