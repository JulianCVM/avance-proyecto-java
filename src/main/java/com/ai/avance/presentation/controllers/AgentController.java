package com.ai.avance.presentation.controllers;

import com.ai.avance.presentation.dto.ChatDTO.AgentDTO;
import com.ai.avance.presentation.dto.request.AiRequest.AgentRequest;
import com.ai.avance.presentation.dto.response.AiResponse.AgentResponse;
import com.ai.avance.services.AiServiceManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestionar agentes de IA.
 */
@RestController
@RequestMapping("/api/agents")
@RequiredArgsConstructor
public class AgentController {

    private final AiServiceManager aiServiceManager;

    @GetMapping("/{id}")
    public ResponseEntity<AgentResponse> getAgentById(@PathVariable Long id) {
        return aiServiceManager.getAgentById(id)
                .map(this::convertToResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AgentResponse> createAgent(@RequestBody AgentRequest request, 
                                                    @RequestHeader("User-Id") Long userId) {
        AgentDTO agentDTO = convertToDTO(request);
        AgentDTO createdAgent = aiServiceManager.createAgent(agentDTO, userId);
        return new ResponseEntity<>(convertToResponse(createdAgent), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgent(@PathVariable Long id) {
        aiServiceManager.deleteAgent(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Convierte un DTO de solicitud a un DTO de agente.
     */
    private AgentDTO convertToDTO(AgentRequest request) {
        return AgentDTO.builder()
                .name(request.getName())
                .description(request.getDescription())
                .purpose(request.getPurpose())
                .tone(request.getTone())
                .domainContext(request.getDomainContext())
                .allowedTopics(request.getAllowedTopics())
                .restrictedTopics(request.getRestrictedTopics())
                .modelConfig(request.getModelConfig())
                .active(true)
                .build();
    }

    /**
     * Convierte un DTO de agente a un DTO de respuesta.
     */
    private AgentResponse convertToResponse(AgentDTO agent) {
        return AgentResponse.builder()
                .id(agent.getId())
                .name(agent.getName())
                .description(agent.getDescription())
                .purpose(agent.getPurpose())
                .tone(agent.getTone())
                .userId(agent.getUserId())
                .domainContext(agent.getDomainContext())
                .allowedTopics(agent.getAllowedTopics())
                .restrictedTopics(agent.getRestrictedTopics())
                .active(agent.isActive())
                .modelConfig(agent.getModelConfig())
                .build();
    }
}