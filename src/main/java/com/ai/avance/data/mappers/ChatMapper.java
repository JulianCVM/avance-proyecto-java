package com.ai.avance.data.mappers;

import com.ai.avance.data.entities.ConversationEntity.ChatSessionEntity;
import com.ai.avance.data.entities.ConversationEntity.MessageEntity;
import com.ai.avance.presentation.dto.ChatDTO.MessageDTO;
import com.ai.avance.presentation.dto.ChatDTO.SessionDTO;

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
        
        return SessionDTO.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .agentId(entity.getAgentId())
                .title(entity.getTitle())
                .createdAt(entity.getCreatedAt())
                .lastActivity(entity.getLastActivity())
                .active(entity.isActive())
                .messages(entity.getMessages().stream()
                        .map(ChatMapper::toMessageDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * Convierte un DTO SessionDTO a una entidad ChatSessionEntity.
     */
    public static ChatSessionEntity toSessionEntity(SessionDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return ChatSessionEntity.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .agentId(dto.getAgentId())
                .title(dto.getTitle())
                .createdAt(dto.getCreatedAt())
                .lastActivity(dto.getLastActivity())
                .active(dto.isActive())
                .messages(dto.getMessages() != null ? 
                        dto.getMessages().stream()
                                .map(ChatMapper::toMessageEntity)
                                .collect(Collectors.toList()) : 
                        List.of())
                .build();
    }

    /**
     * Convierte una entidad MessageEntity a un DTO MessageDTO.
     */
    public static MessageDTO toMessageDTO(MessageEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return MessageDTO.builder()
                .id(entity.getId())
                .chatSessionId(entity.getChatSessionId())
                .content(entity.getContent())
                .role(entity.getRole())
                .timestamp(entity.getTimestamp())
                .modelUsed(entity.getModelUsed())
                .build();
    }

    /**
     * Convierte un DTO MessageDTO a una entidad MessageEntity.
     */
    public static MessageEntity toMessageEntity(MessageDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return MessageEntity.builder()
                .id(dto.getId())
                .chatSessionId(dto.getChatSessionId())
                .content(dto.getContent())
                .role(dto.getRole())
                .timestamp(dto.getTimestamp())
                .modelUsed(dto.getModelUsed())
                .build();
    }
} 