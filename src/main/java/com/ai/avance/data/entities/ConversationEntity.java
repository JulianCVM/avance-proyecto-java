package com.ai.avance.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Clases de entidad para la conversación.
 */
public class ConversationEntity {
    
    /**
     * Representa una sesión de conversación entre un usuario y un agente.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatSessionEntity {
        private Long id;
        private Long userId;
        private Long agentId;
        private String title;
        private LocalDateTime createdAt;
        private LocalDateTime lastActivity;
        private boolean active;
        private List<MessageEntity> messages = new ArrayList<>();
        private String contextInfo;
        private int totalTokensUsed;
    }
    
    /**
     * Representa un mensaje individual dentro de una conversación.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageEntity {
        private Long id;
        private Long chatSessionId;
        private String content;
        private String role; // usuario, asistente, sistema
        private LocalDateTime timestamp;
        private int tokenCount;
        private boolean flagged;
        private String flagReason;
        private String modelUsed;
    }
}
