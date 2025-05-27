package com.ai.avance.presentation.dto.request.AiRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO para la creación o actualización de un agente IA.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentRequest {
    private String name;
    private String description;
    private String purpose;
    private String tone;
    private String styleConfig;
    private String domainContext;
    @Builder.Default
    private List<String> allowedTopics = new ArrayList<>();
    @Builder.Default
    private List<String> restrictedTopics = new ArrayList<>();
    private String modelConfig;
} 