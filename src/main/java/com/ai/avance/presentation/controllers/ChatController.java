package com.ai.avance.presentation.controllers;

import com.ai.avance.presentation.dto.ChatDTO.AgentDTO;
import com.ai.avance.presentation.dto.ChatDTO.MessageDTO;
import com.ai.avance.presentation.dto.ChatDTO.SessionDTO;
import com.ai.avance.services.AiService.AssistantService;
import com.ai.avance.services.AiService.ChatSessionService;
import com.ai.avance.services.AiServiceManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador REST para manejar las solicitudes de chat.
 */
@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final AssistantService assistantService;
    private final AiServiceManager aiServiceManager;
    private final ChatSessionService chatSessionService;

    /**
     * Endpoint para enviar un mensaje a un agente y obtener respuesta.
     */
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMessage(@RequestBody Map<String, Object> request) {
        try {
            String userMessage = (String) request.get("message");
            Long chatSessionId = Long.valueOf(request.get("chatSessionId").toString());
            Long agentId = Long.valueOf(request.get("agentId").toString());
            Long userId = request.get("userId") != null ? 
                    Long.valueOf(request.get("userId").toString()) : 1L; // Usuario por defecto
            
            // Obtener o crear sesión de chat
            SessionDTO chatSession = getChatSession(chatSessionId, agentId, userId);
            
            // Procesar el mensaje y obtener respuesta
            MessageDTO responseMessage = assistantService.processUserMessage(chatSession, userMessage);
            
            // Guardar el mensaje en la base de datos
            chatSessionService.addMessageToSession(chatSession.getId(), responseMessage);
            
            // Crear respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("sessionId", chatSession.getId());
            response.put("role", responseMessage.getRole());
            response.put("content", responseMessage.getContent());
            response.put("timestamp", responseMessage.getTimestamp().toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error al procesar mensaje de chat", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("role", "system");
            errorResponse.put("content", "Error al procesar tu mensaje. Por favor, intenta de nuevo.");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.ok(errorResponse);
        }
    }
    
    /**
     * Obtiene o crea una sesión de chat.
     */
    private SessionDTO getChatSession(Long chatSessionId, Long agentId, Long userId) {
        // Verificar si ya existe la sesión
        if (chatSessionId != null && chatSessionId > 0) {
            Optional<SessionDTO> existingSession = chatSessionService.getSessionById(chatSessionId);
            if (existingSession.isPresent()) {
                return existingSession.get();
            }
        }
        
        // Verificar si el agente existe
        Optional<AgentDTO> agentOpt = aiServiceManager.getAgentDtoById(agentId);
        if (agentOpt.isEmpty()) {
            throw new IllegalArgumentException("Agente no encontrado: " + agentId);
        }
        
        // Crear nueva sesión
        SessionDTO newSession = chatSessionService.createSession(userId, agentId);
        if (newSession == null) {
            throw new IllegalStateException("No se pudo crear la sesión de chat");
        }
        
        return newSession;
    }
}