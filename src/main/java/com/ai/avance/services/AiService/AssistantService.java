package com.ai.avance.services.AiService;

import com.ai.avance.presentation.dto.ChatDTO.MessageDTO;
import com.ai.avance.presentation.dto.ChatDTO.SessionDTO;
import com.ai.avance.presentation.dto.ChatDTO.AgentDTO;
import com.ai.avance.services.AiServiceManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final AiServiceManager aiServiceManager;
    private final ChatSessionService chatSessionService;

    /**
     * Procesa un mensaje del usuario y obtiene una respuesta del asistente IA.
     * 
     * @param chatSession La sesión de chat actual
     * @param messageContent El contenido del mensaje del usuario
     * @return La respuesta generada por el asistente IA
     */
    @Transactional
    public MessageDTO processUserMessage(SessionDTO chatSession, String messageContent) {
        try {
            // Obtener la configuración del agente asociado a esta conversación
            Optional<AgentDTO> agentOpt = aiServiceManager.getAgentDtoById(chatSession.getAgentId());
            
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
            MessageDTO userMessage = createUserMessage(chatSession.getId(), messageContent);
            
            // Guardar mensaje del usuario
            boolean saved = chatSessionService.addMessageToSession(chatSession.getId(), userMessage);
            if (!saved) {
                log.error("No se pudo guardar el mensaje del usuario en la sesión: {}", chatSession.getId());
                return createErrorMessage(chatSession.getId());
            }
            
            // Obtener todos los mensajes de la sesión
            var messages = chatSessionService.getSessionMessages(chatSession.getId());
            
            // Obtener respuesta del modelo IA
            String aiResponse = restClientService.getAiResponse(messages, agent);
            
            // Crear la respuesta del asistente
            MessageDTO assistantMessage = createAssistantMessage(chatSession.getId(), aiResponse, agent.getModelConfig());
            
            // Guardar mensaje del asistente
            chatSessionService.addMessageToSession(chatSession.getId(), assistantMessage);
            
            // Actualizar la sesión de chat
            chatSessionService.updateSessionActivity(chatSession.getId());
            
            return assistantMessage;
            
        } catch (Exception e) {
            log.error("Error al procesar mensaje de usuario", e);
            return createErrorMessage(chatSession.getId());
        }
    }
    
    /**
     * Crea un mensaje de usuario para añadir a la conversación.
     */
    private MessageDTO createUserMessage(Long chatSessionId, String content) {
        return MessageDTO.builder()
                .chatSessionId(chatSessionId)
                .content(content)
                .role("user")
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Crea un mensaje del asistente con la respuesta generada.
     */
    private MessageDTO createAssistantMessage(Long chatSessionId, String content, String modelUsed) {
        return MessageDTO.builder()
                .chatSessionId(chatSessionId)
                .content(content)
                .role("assistant")
                .timestamp(LocalDateTime.now())
                .modelUsed(modelUsed)
                .build();
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