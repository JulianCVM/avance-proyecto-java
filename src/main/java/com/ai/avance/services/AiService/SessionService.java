package com.ai.avance.services.AiService;

import com.ai.avance.presentation.dto.ChatDTO.SessionDTO;
import com.ai.avance.presentation.dto.ChatDTO.AgentDTO;
import com.ai.avance.presentation.dto.ChatDTO.MessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar las sesiones de chat.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SessionService {

    private final AgentService agentService;
    
    // Simulación de base de datos en memoria
    private final Map<Long, SessionDTO> sessions = new ConcurrentHashMap<>();
    private Long nextSessionId = 1L;

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
                .id(nextSessionId++)
                .userId(userId)
                .agentId(agentId)
                .messages(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .lastActivity(LocalDateTime.now())
                .active(true)
                .title("Conversación con " + agent.getName())
                .build();
        
        sessions.put(session.getId(), session);
        log.info("Sesión creada: {}", session.getId());
        
        return session;
    }

    /**
     * Obtiene una sesión existente por su ID.
     *
     * @param sessionId ID de la sesión
     * @return la sesión si existe
     */
    public Optional<SessionDTO> getSessionById(Long sessionId) {
        return Optional.ofNullable(sessions.get(sessionId));
    }

    /**
     * Obtiene todas las sesiones de un usuario.
     *
     * @param userId ID del usuario
     * @return lista de sesiones del usuario
     */
    public List<SessionDTO> getUserSessions(Long userId) {
        return sessions.values().stream()
                .filter(session -> session.getUserId().equals(userId))
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
        SessionDTO session = sessions.get(sessionId);
        
        if (session == null) {
            log.error("Intento de añadir mensaje a sesión inexistente: {}", sessionId);
            return false;
        }
        
        session.getMessages().add(message);
        session.setLastActivity(LocalDateTime.now());
        return true;
    }

    /**
     * Elimina una sesión.
     *
     * @param sessionId ID de la sesión a eliminar
     * @return true si se eliminó correctamente, false si la sesión no existía
     */
    public boolean deleteSession(Long sessionId) {
        SessionDTO removed = sessions.remove(sessionId);
        if (removed != null) {
            log.info("Sesión eliminada: {}", sessionId);
            return true;
        } else {
            log.error("Intento de eliminar sesión inexistente: {}", sessionId);
            return false;
        }
    }

    /**
     * Actualiza la última actividad de una sesión.
     *
     * @param sessionId ID de la sesión
     */
    public void updateSessionActivity(Long sessionId) {
        SessionDTO session = sessions.get(sessionId);
        
        if (session != null) {
            session.setLastActivity(LocalDateTime.now());
        }
    }
} 