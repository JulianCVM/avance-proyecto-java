package com.ai.avance.services.AiService;

import com.ai.avance.data.mappers.ChatMapper;
import com.ai.avance.data.repositories.ChatRepository;
import com.ai.avance.presentation.dto.ChatDTO;
import com.ai.avance.presentation.dto.ChatDTO.MessageDTO;
import com.ai.avance.presentation.dto.ChatDTO.SessionDTO;
import com.ai.avance.presentation.dto.ChatDTO.AgentDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Servicio para gestionar las interacciones con asistentes IA.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssistantService {

    private final RestClientService restClientService;
    private final AgentService agentService;
    private final ChatSessionService chatSessionService;

    /**
     * Procesa un mensaje del usuario y obtiene una respuesta del asistente IA.
     * 
     * @param chatSession La sesión de chat actual
     * @param userMessage El mensaje del usuario
     * @return La respuesta generada por el asistente IA
     */
    public MessageDTO processUserMessage(SessionDTO chatSession, String userMessage) {
        try {
            // Obtener la configuración del agente asociado a esta conversación
            Optional<AgentDTO> agentOpt = agentService.getAgentById(chatSession.getAgentId());
            
            if (agentOpt.isEmpty()) {
                log.error("No se encontró el agente con ID: {}", chatSession.getAgentId());
                return createErrorMessage(chatSession.getId());
            }
            
            AgentDTO agent = agentOpt.get();
            
            // Verificar si el agente está activo
            if (!agent.isActive()) {
                log.warn("Intento de interactuar con un agente inactivo: {}", agent.getId());
                return createDisabledAgentMessage(chatSession.getId());
            }
            
            // Crear y añadir el mensaje del usuario a la conversación
            MessageDTO userMsgEntity = MessageDTO.builder()
                    .chatSessionId(chatSession.getId())
                    .content(userMessage)
                    .role("user")
                    .timestamp(LocalDateTime.now())
                    .build();
            
            // Guardar mensaje del usuario
            chatSessionService.addMessageToSession(chatSession.getId(), userMsgEntity);
            
            // Obtener respuesta del modelo IA
            String aiResponse = restClientService.getAiResponse(
                    chatSessionService.getSessionMessages(chatSession.getId()),
                    agent
            );
            
            // Crear la respuesta del asistente
            MessageDTO assistantMsgEntity = MessageDTO.builder()
                    .chatSessionId(chatSession.getId())
                    .content(aiResponse)
                    .role("assistant")
                    .timestamp(LocalDateTime.now())
                    .modelUsed(agent.getModelConfig())
                    .build();
            
            // Actualizar la sesión de chat
            chatSessionService.updateSessionActivity(chatSession.getId());
            
            return assistantMsgEntity;
            
        } catch (Exception e) {
            log.error("Error al procesar mensaje de usuario", e);
            return createErrorMessage(chatSession.getId());
        }
    }
    
    /**
     * Crea un mensaje de error para situaciones en las que no se puede procesar la solicitud.
     */
    private MessageDTO createErrorMessage(Long chatSessionId) {
        return MessageDTO.builder()
                .chatSessionId(chatSessionId)
                .content("Lo siento, ha ocurrido un error al procesar tu mensaje. Por favor, intenta de nuevo más tarde.")
                .role("assistant")
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Crea un mensaje para informar que el agente está desactivado.
     */
    private MessageDTO createDisabledAgentMessage(Long chatSessionId) {
        return MessageDTO.builder()
                .chatSessionId(chatSessionId)
                .content("Este asistente está actualmente desactivado. Por favor, contacta con el administrador o elige otro asistente.")
                .role("system")
                .timestamp(LocalDateTime.now())
                .build();
    }
} 