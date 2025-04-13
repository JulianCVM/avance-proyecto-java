package com.ai.avance.services.AiService;

import com.ai.avance.data.entities.ConversationEntity.ChatSessionEntity;
import com.ai.avance.data.entities.ConversationEntity.MessageEntity;
import com.ai.avance.data.mappers.ChatMapper;
import com.ai.avance.data.repositories.ChatRepository;
import com.ai.avance.presentation.dto.ChatDTO.AgentDTO;
import com.ai.avance.presentation.dto.ChatDTO.MessageDTO;
import com.ai.avance.presentation.dto.ChatDTO.SessionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar las sesiones de chat.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ChatSessionService {

    private final AgentService agentService;
    private final ChatRepository chatRepository;

    /**
     * Crea una nueva sesión de chat.
     *
     * @param userId ID del usuario que crea la sesión
     * @param agentId ID del agente con el que se establece la sesión
     * @return la sesión de chat creada o null si el agente no existe
     */
    public SessionDTO createSession(Long userId, Long agentId) {
        Optional<AgentDTO> agentOpt = agentService.getAgentById(agentId);
        
        if (agentOpt.isEmpty()) {
            log.error("No se puede crear sesión con agente inexistente: {}", agentId);
            return null;
        }
        
        AgentDTO agent = agentOpt.get();
        
        SessionDTO session = SessionDTO.builder()
                .userId(userId)
                .agentId(agentId)
                .title("Conversación con " + agent.getName())
                .createdAt(LocalDateTime.now())
                .lastActivity(LocalDateTime.now())
                .active(true)
                .build();
        
        // Convertir DTO a entidad y guardar en el repositorio
        ChatSessionEntity sessionEntity = ChatMapper.toSessionEntity(session);
        sessionEntity = chatRepository.saveSession(sessionEntity);
        
        // Convertir entidad a DTO para devolver
        SessionDTO savedSession = ChatMapper.toSessionDTO(sessionEntity);
        log.info("Sesión creada: {}", savedSession.getId());
        
        return savedSession;
    }

    /**
     * Obtiene una sesión existente por su ID.
     *
     * @param sessionId ID de la sesión
     * @return la sesión si existe
     */
    public Optional<SessionDTO> getSessionById(Long sessionId) {
        return chatRepository.findSessionById(sessionId)
                .map(ChatMapper::toSessionDTO);
    }

    /**
     * Obtiene todas las sesiones de un usuario.
     *
     * @param userId ID del usuario
     * @return lista de sesiones del usuario
     */
    public List<SessionDTO> getUserSessions(Long userId) {
        return chatRepository.findSessionsByUserId(userId)
                .stream()
                .map(ChatMapper::toSessionDTO)
                .collect(Collectors.toList());
    }

    /**
     * Añade un mensaje a una sesión existente.
     *
     * @param sessionId ID de la sesión
     * @param message mensaje a añadir
     * @return true si se añadió correctamente, false si la sesión no existe
     */
    public boolean addMessageToSession(Long sessionId, MessageDTO message) {
        Optional<ChatSessionEntity> sessionOpt = chatRepository.findSessionById(sessionId);
        
        if (sessionOpt.isEmpty()) {
            log.error("Intento de añadir mensaje a sesión inexistente: {}", sessionId);
            return false;
        }
        
        ChatSessionEntity session = sessionOpt.get();
        MessageEntity messageEntity = ChatMapper.toMessageEntity(message);
        chatRepository.saveMessage(messageEntity);
        
        // Actualizar la fecha de última actividad
        session.setLastActivity(LocalDateTime.now());
        chatRepository.saveSession(session);
        
        return true;
    }

    /**
     * Elimina una sesión.
     *
     * @param sessionId ID de la sesión a eliminar
     * @return true si se eliminó correctamente, false si la sesión no existía
     */
    public boolean deleteSession(Long sessionId) {
        Optional<ChatSessionEntity> sessionOpt = chatRepository.findSessionById(sessionId);
        
        if (sessionOpt.isEmpty()) {
            log.error("Intento de eliminar sesión inexistente: {}", sessionId);
            return false;
        }
        
        chatRepository.deleteSessionById(sessionId);
        log.info("Sesión eliminada: {}", sessionId);
        return true;
    }

    /**
     * Actualiza la última actividad de una sesión.
     *
     * @param sessionId ID de la sesión
     */
    public void updateSessionActivity(Long sessionId) {
        chatRepository.findSessionById(sessionId)
                .ifPresent(session -> {
                    session.setLastActivity(LocalDateTime.now());
                    chatRepository.saveSession(session);
                });
    }
    
    /**
     * Obtiene todos los mensajes de una sesión de chat.
     *
     * @param sessionId ID de la sesión
     * @return lista de mensajes de la sesión
     */
    public List<MessageDTO> getSessionMessages(Long sessionId) {
        return chatRepository.findMessagesByChatSessionId(sessionId)
                .stream()
                .map(ChatMapper::toMessageDTO)
                .collect(Collectors.toList());
    }
} 