package com.ai.avance.services.AiService;

import com.ai.avance.presentation.dto.ChatDTO.AgentDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar la lógica de negocio relacionada con los agentes de IA.
 */
@Service
@RequiredArgsConstructor
public class AgentService {

    // Simulación de base de datos en memoria
    private final ConcurrentHashMap<Long, AgentDTO> agents = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    /**
     * Crea un nuevo agente para un usuario.
     */
    public AgentDTO createAgent(AgentDTO agent, Long userId) {
        agent.setId(idCounter.getAndIncrement());
        agent.setActive(true);
        agents.put(agent.getId(), agent);
        return agent;
    }

    /**
     * Actualiza la configuración de un agente existente.
     */
    public Optional<AgentDTO> updateAgent(Long agentId, AgentDTO updatedAgent) {
        return Optional.ofNullable(agents.get(agentId))
                .map(existingAgent -> {
                    // Actualizar solo los campos proporcionados
                    if (updatedAgent.getName() != null) {
                        existingAgent.setName(updatedAgent.getName());
                    }
                    if (updatedAgent.getDescription() != null) {
                        existingAgent.setDescription(updatedAgent.getDescription());
                    }
                    if (updatedAgent.getPurpose() != null) {
                        existingAgent.setPurpose(updatedAgent.getPurpose());
                    }
                    if (updatedAgent.getTone() != null) {
                        existingAgent.setTone(updatedAgent.getTone());
                    }
                    if (updatedAgent.getDomainContext() != null) {
                        existingAgent.setDomainContext(updatedAgent.getDomainContext());
                    }
                    if (updatedAgent.getAllowedTopics() != null && !updatedAgent.getAllowedTopics().isEmpty()) {
                        existingAgent.setAllowedTopics(updatedAgent.getAllowedTopics());
                    }
                    if (updatedAgent.getRestrictedTopics() != null && !updatedAgent.getRestrictedTopics().isEmpty()) {
                        existingAgent.setRestrictedTopics(updatedAgent.getRestrictedTopics());
                    }
                    if (updatedAgent.getModelConfig() != null) {
                        existingAgent.setModelConfig(updatedAgent.getModelConfig());
                    }
                    
                    agents.put(existingAgent.getId(), existingAgent);
                    return existingAgent;
                });
    }

    /**
     * Obtiene todos los agentes de un usuario.
     */
    public List<AgentDTO> getUserAgents(Long userId) {
        return agents.values().stream()
                .filter(agent -> agent.getUserId() != null && agent.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    /**
     * Busca un agente por su ID.
     */
    public Optional<AgentDTO> getAgentById(Long agentId) {
        return Optional.ofNullable(agents.get(agentId));
    }

    /**
     * Cambia el estado de activación de un agente.
     */
    public Optional<AgentDTO> toggleAgentActivation(Long agentId) {
        return Optional.ofNullable(agents.get(agentId))
                .map(agent -> {
                    agent.setActive(!agent.isActive());
                    agents.put(agent.getId(), agent);
                    return agent;
                });
    }

    /**
     * Elimina un agente.
     */
    public void deleteAgent(Long agentId) {
        agents.remove(agentId);
    }

    /**
     * Busca agentes por término de búsqueda.
     */
    public List<AgentDTO> searchAgents(String searchTerm) {
        String lowercaseSearchTerm = searchTerm.toLowerCase();
        return agents.values().stream()
                .filter(agent -> 
                    (agent.getName() != null && agent.getName().toLowerCase().contains(lowercaseSearchTerm)) ||
                    (agent.getDescription() != null && agent.getDescription().toLowerCase().contains(lowercaseSearchTerm))
                )
                .collect(Collectors.toList());
    }
    
    /**
     * Inicializa algunos agentes de demostración
     */
    public void initDemoAgents() {
        if (agents.isEmpty()) {
            // Crear un agente de asistente general
            AgentDTO generalAssistant = AgentDTO.builder()
                .name("Asistente General")
                .description("Un asistente de propósito general para responder preguntas diversas")
                .purpose("Ayudar con consultas generales y proporcionar información útil sobre diversos temas")
                .tone("amigable")
                .domainContext("Conocimiento general y asistencia diaria")
                .allowedTopics(List.of("ciencia", "tecnología", "cultura", "historia", "deportes"))
                .restrictedTopics(List.of("política controversial", "contenido para adultos", "ilegalidades"))
                .modelConfig("gpt-3.5-turbo")
                .active(true)
                .userId(1L)
                .build();
                
            // Crear un agente especializado en programación
            AgentDTO programmingAssistant = AgentDTO.builder()
                .name("Asistente de Programación")
                .description("Especializado en ayudar con dudas de programación y desarrollo de software")
                .purpose("Proporcionar asistencia técnica en programación, debugging y diseño de software")
                .tone("técnico")
                .domainContext("Programación, desarrollo de software, algoritmos, bases de datos")
                .allowedTopics(List.of("Java", "Python", "JavaScript", "SQL", "algoritmos", "patrones de diseño"))
                .modelConfig("gpt-4")
                .active(true)
                .userId(1L)
                .build();
                
            // Guardarlos
            createAgent(generalAssistant, 1L);
            createAgent(programmingAssistant, 1L);
        }
    }
} 