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
 * Clases de entidad para la conversación.
 */
public class ConversationEntities {
    
    /**
     * Representa una sesión de conversación entre un usuario y un agente.
     */
    @Entity
    @Table(name = "chat_sessions")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatSessionEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id")
        private UserEntities.UserEntity user;
        
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "agent_id")
        private AIEntities.AgentEntity agent;
        
        @Column
        private String title;
        
        @Column(name = "created_at")
        private LocalDateTime createdAt;
        
        @Column(name = "last_activity")
        private LocalDateTime lastActivity;
        
        @Column
        private boolean active;
        
        @OneToMany(mappedBy = "chatSession", cascade = CascadeType.ALL, orphanRemoval = true)
        @Builder.Default
        private List<MessageEntity> messages = new ArrayList<>();
        
        @Column(name = "context_info", columnDefinition = "TEXT")
        private String contextInfo;
        
        @Column(name = "total_tokens_used")
        private int totalTokensUsed;
        
        @PrePersist
        protected void onCreate() {
            createdAt = LocalDateTime.now();
            lastActivity = LocalDateTime.now();
        }
        
        @PreUpdate
        protected void onUpdate() {
            lastActivity = LocalDateTime.now();
        }
    }
    
    /**
     * Representa un mensaje individual dentro de una conversación.
     */
    @Entity
    @Table(name = "messages")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "chat_session_id")
        private ChatSessionEntity chatSession;
        
        @Column(columnDefinition = "TEXT")
        private String content;
        
        @Column(nullable = false)
        private String role; // usuario, asistente, sistema
        
        @Column
        private LocalDateTime timestamp;
        
        @Column(name = "token_count")
        private int tokenCount;
        
        @Column
        private boolean flagged;
        
        @Column(name = "flag_reason")
        private String flagReason;
        
        @Column(name = "model_used")
        private String modelUsed;
        
        @PrePersist
        protected void onCreate() {
            timestamp = LocalDateTime.now();
        }
    }
    
    /**
     * Representa las conversaciones con el chatbot por defecto del sistema.
     */
    @Entity
    @Table(name = "default_chat_sessions")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DefaultChatSessionEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id")
        private UserEntities.UserEntity user;
        
        @Column(name = "session_key", unique = true)
        private String sessionKey;
        
        @Column(name = "created_at")
        private LocalDateTime createdAt;
        
        @Column(name = "last_activity")
        private LocalDateTime lastActivity;
        
        @OneToMany(mappedBy = "chatSession", cascade = CascadeType.ALL, orphanRemoval = true)
        @Builder.Default
        private List<DefaultMessageEntity> messages = new ArrayList<>();
        
        @PrePersist
        protected void onCreate() {
            createdAt = LocalDateTime.now();
            lastActivity = LocalDateTime.now();
        }
        
        @PreUpdate
        protected void onUpdate() {
            lastActivity = LocalDateTime.now();
        }
    }
    
    /**
     * Representa un mensaje en la conversación con el chatbot por defecto.
     */
    @Entity
    @Table(name = "default_messages")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DefaultMessageEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "chat_session_id")
        private DefaultChatSessionEntity chatSession;
        
        @Column(columnDefinition = "TEXT")
        private String content;
        
        @Column(nullable = false)
        private String role; // usuario, asistente, sistema
        
        @Column
        private LocalDateTime timestamp;
        
        @PrePersist
        protected void onCreate() {
            timestamp = LocalDateTime.now();
        }
    }
}
