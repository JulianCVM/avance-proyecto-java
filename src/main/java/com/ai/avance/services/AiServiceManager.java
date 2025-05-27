package com.ai.avance.services;

import com.ai.avance.data.entities.AIEntities.AgentEntity;
import com.ai.avance.data.entities.ConversationEntities.ChatSessionEntity;
import com.ai.avance.data.entities.ConversationEntities.DefaultChatSessionEntity;
import com.ai.avance.data.entities.ConversationEntities.DefaultMessageEntity;
import com.ai.avance.data.entities.ConversationEntities.MessageEntity;
import com.ai.avance.data.entities.UserEntities.UserEntity;
import com.ai.avance.presentation.dto.ChatDTO.AgentDTO;
import com.ai.avance.presentation.dto.ChatDTO.SessionDTO;
import com.ai.avance.services.AiService.AgentService;
import com.ai.avance.services.AiService.ChatSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Clase principal para servicios de IA.
 * Gestiona y coordina todos los servicios relacionados con la IA.
 */
@Slf4j
@Service
public class AiServiceManager {
    
    private final AgentService agentDtoService;
    private final ChatService chatService;
    private final DefaultChatService defaultChatService;
    private final ChatSessionService chatSessionService;
    
    @Autowired
    public AiServiceManager(
            AgentService agentDtoService,
            ChatService chatService,
            DefaultChatService defaultChatService,
            ChatSessionService chatSessionService) {
        this.agentDtoService = agentDtoService;
        this.chatService = chatService;
        this.defaultChatService = defaultChatService;
        this.chatSessionService = chatSessionService;
    }
    
    // Métodos de gestión de agentes DTO
    public AgentDTO createAgentDto(AgentDTO agent, Long userId) {
        return agentDtoService.createAgent(agent, userId);
    }
    
    public List<AgentDTO> getUserAgentDtos(Long userId) {
        return agentDtoService.getUserAgents(userId);
    }
    
    public Optional<AgentDTO> getAgentDtoById(Long id) {
        return agentDtoService.getAgentById(id);
    }
    
    public Optional<AgentDTO> updateAgentDto(Long id, AgentDTO agent) {
        return agentDtoService.updateAgent(id, agent);
    }
    
    public Optional<AgentDTO> toggleAgentDtoActivation(Long id) {
        return agentDtoService.toggleAgentActivation(id);
    }
    
    public void deleteAgentDto(Long id) {
        agentDtoService.deleteAgent(id);
    }
    
    public List<AgentDTO> searchAgentDtos(String query) {
        return agentDtoService.searchAgents(query);
    }
    
    /**
     * Inicializa agentes de demostración
     */
    public void initDemoAgents() {
        agentDtoService.initDemoAgents();
    }
    
    /**
     * Obtiene las sesiones de chat más recientes de un usuario.
     * 
     * @param userId ID del usuario
     * @param limit Número máximo de sesiones a devolver
     * @return Lista de sesiones recientes, ordenadas por fecha de última actividad
     */
    public List<SessionDTO> getUserRecentSessions(Long userId, int limit) {
        try {
            List<SessionDTO> sessions = chatSessionService.getUserSessions(userId);
            
            // Ordenar por última actividad (más reciente primero) y limitar resultados
            return sessions.stream()
                    .sorted(Comparator.comparing(SessionDTO::getLastActivity).reversed())
                    .limit(limit)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error al obtener las sesiones recientes del usuario {}: {}", userId, e.getMessage());
            return Collections.emptyList();
        }
    }
    
    // Métodos de gestión de ChatService
    
    public ChatSessionEntity saveSession(ChatSessionEntity session) {
        return chatService.saveSession(session);
    }
    
    public Optional<ChatSessionEntity> findSessionById(Long id) {
        return chatService.findSessionById(id);
    }
    
    public List<ChatSessionEntity> findSessionsByUser(UserEntity user) {
        return chatService.findSessionsByUser(user);
    }
    
    public List<ChatSessionEntity> findSessionsByAgent(AgentEntity agent) {
        return chatService.findSessionsByAgent(agent);
    }
    
    public void deleteSessionById(Long id) {
        chatService.deleteSessionById(id);
    }
    
    public MessageEntity saveMessage(MessageEntity message) {
        return chatService.saveMessage(message);
    }
    
    public List<MessageEntity> findMessagesByChatSession(ChatSessionEntity chatSession) {
        return chatService.findMessagesByChatSession(chatSession);
    }
    
    public List<MessageEntity> findMessagesByChatSessionId(Long chatSessionId) {
        return chatService.findMessagesByChatSessionId(chatSessionId);
    }
    
    // Métodos de gestión de DefaultChatService
    
    public DefaultChatSessionEntity createDefaultSession(UserEntity user) {
        return defaultChatService.createSession(user);
    }
    
    public Optional<DefaultChatSessionEntity> findDefaultSessionBySessionKey(String sessionKey) {
        return defaultChatService.findSessionBySessionKey(sessionKey);
    }
    
    public List<DefaultChatSessionEntity> findDefaultSessionsByUser(UserEntity user) {
        return defaultChatService.findSessionsByUser(user);
    }
    
    public DefaultMessageEntity saveDefaultMessage(DefaultMessageEntity message) {
        return defaultChatService.saveMessage(message);
    }
    
    public List<DefaultMessageEntity> findDefaultMessagesByChatSession(DefaultChatSessionEntity chatSession) {
        return defaultChatService.findMessagesByChatSession(chatSession);
    }
    
    public void deleteDefaultSessionById(Long id) {
        defaultChatService.deleteSessionById(id);
    }
}