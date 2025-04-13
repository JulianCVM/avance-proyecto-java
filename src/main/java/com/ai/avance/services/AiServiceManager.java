package com.ai.avance.services;

import com.ai.avance.presentation.dto.ChatDTO.AgentDTO;
import com.ai.avance.services.AiService.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Clase principal para servicios de IA.
 * Los servicios específicos se implementan en sub-paquetes.
 */
@Service
public class AiServiceManager {
    
    private final AgentService agentService;
    
    @Autowired
    public AiServiceManager(AgentService agentService) {
        this.agentService = agentService;
    }
    
    public AgentDTO createAgent(AgentDTO agent, Long userId) {
        return agentService.createAgent(agent, userId);
    }
    
    public List<AgentDTO> getUserAgents(Long userId) {
        return agentService.getUserAgents(userId);
    }
    
    public Optional<AgentDTO> getAgentById(Long id) {
        return agentService.getAgentById(id);
    }
    
    public Optional<AgentDTO> updateAgent(Long id, AgentDTO agent) {
        return agentService.updateAgent(id, agent);
    }
    
    public Optional<AgentDTO> toggleAgentActivation(Long id) {
        return agentService.toggleAgentActivation(id);
    }
    
    public void deleteAgent(Long id) {
        agentService.deleteAgent(id);
    }
    
    public List<AgentDTO> searchAgents(String query) {
        return agentService.searchAgents(query);
    }
    
    /**
     * Inicializa agentes de demostración
     */
    public void initDemoAgents() {
        agentService.initDemoAgents();
    }
}