package com.ai.avance.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Clases de entidad para la IA.
 */
public class AIEntities {
    
    /**
     * Entidad que representa un agente de IA.
     */
    @Entity
    @Table(name = "agents")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AgentEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        
        @Column(nullable = false)
        private String name;
        
        @Column(columnDefinition = "TEXT")
        private String description;
        
        @Column(columnDefinition = "TEXT")
        private String purpose;
        
        @Column
        private String tone;
        
        @ElementCollection
        @CollectionTable(name = "agent_allowed_topics", 
                joinColumns = @JoinColumn(name = "agent_id"))
        @Column(name = "topic")
        @Builder.Default
        private List<String> allowedTopics = new ArrayList<>();
        
        @ElementCollection
        @CollectionTable(name = "agent_restricted_topics", 
                joinColumns = @JoinColumn(name = "agent_id"))
        @Column(name = "topic")
        @Builder.Default
        private List<String> restrictedTopics = new ArrayList<>();
        
        @Column(nullable = false)
        private boolean active;
        
        @Column(name = "model_config", columnDefinition = "TEXT")
        private String modelConfig;
        
        @Column(name = "domain_context", columnDefinition = "TEXT")
        private String domainContext;
        
        @Column(name = "api_token", nullable = false)
        private String apiToken;
        
        @Column(name = "model_provider")
        private String modelProvider; // openai, gemini, etc.
        
        @Column(name = "created_at")
        private LocalDateTime createdAt;
        
        @Column(name = "updated_at")
        private LocalDateTime updatedAt;
        
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id")
        private UserEntities.UserEntity user;
        
        @OneToMany(mappedBy = "agent", cascade = CascadeType.ALL, orphanRemoval = true)
        @Builder.Default
        private List<ConversationEntities.ChatSessionEntity> chatSessions = new ArrayList<>();
        
        @PrePersist
        protected void onCreate() {
            createdAt = LocalDateTime.now();
            updatedAt = LocalDateTime.now();
        }
        
        @PreUpdate
        protected void onUpdate() {
            updatedAt = LocalDateTime.now();
        }
    }
    
    /**
     * Entidad que representa el agente por defecto del sistema (chatbot informativo).
     */
    @Entity
    @Table(name = "default_agent")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DefaultAgentEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        
        @Column(nullable = false)
        private String name;
        
        @Column(columnDefinition = "TEXT")
        private String description;
        
        @Column(columnDefinition = "TEXT")
        private String systemPrompt;
        
        @Column(name = "api_token")
        private String apiToken;
        
        @Column(name = "model_provider", nullable = false)
        private String modelProvider; // gemini
        
        @Column(name = "active", nullable = false)
        private boolean active;
        
        @Column(name = "created_at")
        private LocalDateTime createdAt;
        
        @Column(name = "updated_at")
        private LocalDateTime updatedAt;
        
        @PrePersist
        protected void onCreate() {
            createdAt = LocalDateTime.now();
            updatedAt = LocalDateTime.now();
        }
        
        @PreUpdate
        protected void onUpdate() {
            updatedAt = LocalDateTime.now();
        }
    }
} 