package com.ai.avance.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO simplificado para las comunicaciones de chat.
 * Esto se usa temporalmente para evitar problemas con las entidades.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatDTO {
    
    private Long id;
    private Long userId;
    private Long agentId;
    private String content;
    private String role;
    private LocalDateTime timestamp;
    private String modelUsed;
    
    /**
     * Representa un mensaje completo para la API.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageDTO {
        private Long id;
        private Long chatSessionId;
        private String content;
        private String role;
        private LocalDateTime timestamp;
        private String modelUsed;
    }
    
    /**
     * Representa una sesión de chat.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SessionDTO {
        private Long id;
        private Long userId;
        private Long agentId;
        private String title;
        private LocalDateTime createdAt;
        private LocalDateTime lastActivity;
        private boolean active;
        @Builder.Default
        private List<MessageDTO> messages = new ArrayList<>();
    }
    
    /**
     * Representa un agente de IA.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AgentDTO {
        private Long id;
        private String name;
        private String description;
        private String purpose;
        private String tone;
        @Builder.Default
        private List<String> allowedTopics = new ArrayList<>();
        @Builder.Default
        private List<String> restrictedTopics = new ArrayList<>();
        private boolean active;
        private String modelConfig;
        private String domainContext;
        private Long userId;
    }
} 