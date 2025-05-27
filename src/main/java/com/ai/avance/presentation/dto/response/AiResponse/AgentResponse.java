package com.ai.avance.presentation.dto.response.AiResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO para respuestas relacionadas con agentes IA.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentResponse {
    private Long id;
    private String name;
    private String description;
    private String purpose;
    private String tone;
    private String styleConfig;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;
    private String domainContext;
    @Builder.Default
    private List<String> allowedTopics = new ArrayList<>();
    @Builder.Default
    private List<String> restrictedTopics = new ArrayList<>();
    private boolean active;
    private String modelConfig;
} 