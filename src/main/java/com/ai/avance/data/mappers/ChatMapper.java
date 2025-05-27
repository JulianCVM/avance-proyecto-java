package com.ai.avance.data.mappers;

import com.ai.avance.data.entities.AIEntities.AgentEntity;
import com.ai.avance.data.entities.ConversationEntities.ChatSessionEntity;
import com.ai.avance.data.entities.ConversationEntities.MessageEntity;
import com.ai.avance.data.entities.UserEntities.UserEntity;
import com.ai.avance.presentation.dto.ChatDTO.MessageDTO;
import com.ai.avance.presentation.dto.ChatDTO.SessionDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapeador para convertir entre entidades y DTOs de chat.
 */
public class ChatMapper {

    /**
     * Convierte una entidad ChatSessionEntity a un DTO SessionDTO.
     */
    public static SessionDTO toSessionDTO(ChatSessionEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Long userId = entity.getUser() != null ? entity.getUser().getId() : null;
        Long agentId = entity.getAgent() != null ? entity.getAgent().getId() : null;
        
        SessionDTO dto = SessionDTO.builder()
                .id(entity.getId())
                .userId(userId)
                .agentId(agentId)
                .title(entity.getTitle())
                .createdAt(entity.getCreatedAt())
                .lastActivity(entity.getLastActivity())
                .active(entity.isActive())
                .build();
        
        // Convertir mensajes sólo si están disponibles
        if (entity.getMessages() != null && !entity.getMessages().isEmpty()) {
            dto.setMessages(entity.getMessages().stream()
                    .map(ChatMapper::toMessageDTO)
                    .collect(Collectors.toList()));
        } else {
            dto.setMessages(new ArrayList<>());
        }
        
        return dto;
    }

    /**
     * Convierte un DTO SessionDTO a una entidad ChatSessionEntity.
     * Nota: Esta conversión no incluye entidades completas de usuario y agente,
     * sólo referencias por ID que deberán ser resueltas posteriormente.
     */
    public static ChatSessionEntity toSessionEntity(SessionDTO dto) {
        if (dto == null) {
            return null;
        }
        
        // Crear referencias básicas a usuario y agente
        UserEntity userRef = null;
        AgentEntity agentRef = null;
        
        if (dto.getUserId() != null) {
            userRef = new UserEntity();
            userRef.setId(dto.getUserId());
        }
        
        if (dto.getAgentId() != null) {
            agentRef = new AgentEntity();
            agentRef.setId(dto.getAgentId());
        }
        
        ChatSessionEntity entity = new ChatSessionEntity();
        entity.setId(dto.getId());
        entity.setUser(userRef);
        entity.setAgent(agentRef);
        entity.setTitle(dto.getTitle());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setLastActivity(dto.getLastActivity());
        entity.setActive(dto.isActive());
        entity.setMessages(new ArrayList<>());
        
        // Convertir mensajes si existen
        if (dto.getMessages() != null && !dto.getMessages().isEmpty()) {
            List<MessageEntity> messageEntities = dto.getMessages().stream()
                    .map(messageDto -> {
                        MessageEntity messageEntity = toMessageEntity(messageDto);
                        messageEntity.setChatSession(entity); // Establecer relación bidireccional
                        return messageEntity;
                    })
                    .collect(Collectors.toList());
            entity.setMessages(messageEntities);
        }
        
        return entity;
    }

    /**
     * Convierte una entidad MessageEntity a un DTO MessageDTO.
     */
    public static MessageDTO toMessageDTO(MessageEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Long chatSessionId = entity.getChatSession() != null ? entity.getChatSession().getId() : null;
        
        return MessageDTO.builder()
                .id(entity.getId())
                .chatSessionId(chatSessionId)
                .content(entity.getContent())
                .role(entity.getRole())
                .timestamp(entity.getTimestamp())
                .modelUsed(entity.getModelUsed())
                .build();
    }

    /**
     * Convierte un DTO MessageDTO a una entidad MessageEntity.
     * Nota: La referencia a la sesión de chat debe ser establecida después de la conversión.
     */
    public static MessageEntity toMessageEntity(MessageDTO dto) {
        if (dto == null) {
            return null;
        }
        
        MessageEntity entity = new MessageEntity();
        entity.setId(dto.getId());
        entity.setContent(dto.getContent());
        entity.setRole(dto.getRole());
        entity.setTimestamp(dto.getTimestamp() != null ? dto.getTimestamp() : java.time.LocalDateTime.now());
        entity.setModelUsed(dto.getModelUsed());
        
        // Si existe un ID de sesión, crear una referencia básica
        if (dto.getChatSessionId() != null) {
            ChatSessionEntity sessionRef = new ChatSessionEntity();
            sessionRef.setId(dto.getChatSessionId());
            entity.setChatSession(sessionRef);
        }
        
        return entity;
    }
} 