package com.ai.avance.data.repositories;

import com.ai.avance.data.entities.ConversationEntity.ChatSessionEntity;
import com.ai.avance.data.entities.ConversationEntity.MessageEntity;
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
        sessions.put(session.getId(), session);
        return session;
    }

    @Override
    public Optional<ChatSessionEntity> findSessionById(Long id) {
        return Optional.ofNullable(sessions.get(id));
    }

    @Override
    public List<ChatSessionEntity> findSessionsByUserId(Long userId) {
        return sessions.values().stream()
                .filter(session -> session.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatSessionEntity> findSessionsByAgentId(Long agentId) {
        return sessions.values().stream()
                .filter(session -> session.getAgentId().equals(agentId))
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
        messages.put(message.getId(), message);
        return message;
    }

    @Override
    public List<MessageEntity> findMessagesByChatSessionId(Long chatSessionId) {
        return messages.values().stream()
                .filter(message -> message.getChatSessionId().equals(chatSessionId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteMessagesByChatSessionId(Long chatSessionId) {
        List<Long> messageIdsToDelete = messages.values().stream()
                .filter(message -> message.getChatSessionId().equals(chatSessionId))
                .map(MessageEntity::getId)
                .collect(Collectors.toList());
        
        messageIdsToDelete.forEach(messages::remove);
    }
} 