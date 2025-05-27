package com.ai.avance.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Clases de entidad para tokens de API.
 */
public class TokenEntities {
    
    /**
     * Entidad que representa un token de API almacenado por un usuario.
     */
    @Entity
    @Table(name = "api_tokens")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiTokenEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        private UserEntities.UserEntity user;
        
        @Column(name = "token_value", nullable = false)
        private String tokenValue;
        
        @Column(name = "provider", nullable = false)
        private String provider; // openai, gemini, etc.
        
        @Column(name = "is_active", nullable = false)
        private boolean isActive;
        
        @Column(name = "name")
        private String name;
        
        @Column(name = "created_at")
        private LocalDateTime createdAt;
        
        @Column(name = "last_used")
        private LocalDateTime lastUsed;
        
        @PrePersist
        protected void onCreate() {
            createdAt = LocalDateTime.now();
        }
    }
    
    /**
     * Entidad que representa el uso de tokens para fines de seguimiento y facturación.
     */
    @Entity
    @Table(name = "token_usage")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenUsageEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        private UserEntities.UserEntity user;
        
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "agent_id")
        private AIEntities.AgentEntity agent;
        
        @Column(name = "input_tokens")
        private int inputTokens;
        
        @Column(name = "output_tokens")
        private int outputTokens;
        
        @Column(name = "model", nullable = false)
        private String model;
        
        @Column(name = "provider", nullable = false)
        private String provider;
        
        @Column(name = "timestamp")
        private LocalDateTime timestamp;
        
        @Column(name = "session_id")
        private String sessionId;
        
        @PrePersist
        protected void onCreate() {
            timestamp = LocalDateTime.now();
        }
    }
} 