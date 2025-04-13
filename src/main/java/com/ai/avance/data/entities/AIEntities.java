package com.ai.avance.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Clases de entidad para la IA.
 */
public class AIEntities {
    
    /**
     * Entidad que representa un agente de IA.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AgentEntity {
        private Long id;
        private String name;
        private String description;
        private String purpose;
        private String tone;
        private List<String> allowedTopics = new ArrayList<>();
        private List<String> restrictedTopics = new ArrayList<>();
        private boolean active;
        private String modelConfig;
        private String domainContext;
        private Long userId;
    }
} 