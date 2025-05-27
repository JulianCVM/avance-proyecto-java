package com.ai.avance.services.AiService;

import com.ai.avance.data.entities.AIEntities.AgentEntity;
import com.ai.avance.data.entities.ConversationEntities.ChatSessionEntity;
import com.ai.avance.data.entities.ConversationEntities.MessageEntity;
import com.ai.avance.data.entities.UserEntities.UserEntity;
import com.ai.avance.data.mappers.ChatMapper;
import com.ai.avance.data.repositories.AgentRepository;
import com.ai.avance.data.repositories.ChatRepository;
import com.ai.avance.data.repositories.UserRepository;
import com.ai.avance.presentation.dto.ChatDTO.MessageDTO;
import com.ai.avance.presentation.dto.ChatDTO.SessionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar las sesiones de chat.
 * Integra con el repositorio de entidades y proporciona DTOs para la capa de presentación.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ChatSessionService {

    private final AgentRepository agentRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    /**
     * Crea una nueva sesión de chat.
     *
     * @param userId ID del usuario que crea la sesión
     * @param agentId ID del agente con el que se establece la sesión
     * @return la sesión de chat creada o null si el agente o usuario no existe
     */
    @Transactional
    public SessionDTO createSession(Long userId, Long agentId) {
        Optional<AgentEntity> agentOpt = agentRepository.findById(agentId);
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        
        if (agentOpt.isEmpty()) {
            log.error("No se puede crear sesión con agente inexistente: {}", agentId);
            return null;
        }
        
        if (userOpt.isEmpty()) {
            log.error("No se puede crear sesión para usuario inexistente: {}", userId);
            return null;
        }
        
        AgentEntity agent = agentOpt.get();
        
        // Crear entidad de sesión
        ChatSessionEntity sessionEntity = new ChatSessionEntity();
        sessionEntity.setUser(userOpt.get());
        sessionEntity.setAgent(agent);
        sessionEntity.setTitle("Conversación con " + agent.getName());
        sessionEntity.setCreatedAt(LocalDateTime.now());
        sessionEntity.setLastActivity(LocalDateTime.now());
        sessionEntity.setActive(true);
        sessionEntity.setMessages(new ArrayList<>());
        
        // Guardar en el repositorio
        ChatSessionEntity savedEntity = chatRepository.saveSession(sessionEntity);
        
        // Convertir a DTO y devolver
        SessionDTO sessionDTO = ChatMapper.toSessionDTO(savedEntity);
        log.info("Sesión creada: {}", sessionDTO.getId());
        
        return sessionDTO;
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
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        
        if (userOpt.isEmpty()) {
            log.error("No se encontró el usuario con ID: {}", userId);
            return List.of();
        }
        
        return chatRepository.findSessionsByUserId(userId)
                .stream()
                .map(ChatMapper::toSessionDTO)
                .collect(Collectors.toList());
    }

    /**
     * Añade un mensaje a una sesión existente.
     *
     * @param sessionId ID de la sesión
     * @param messageDTO mensaje a añadir
     * @return true si se añadió correctamente, false si la sesión no existe
     */
    @Transactional
    public boolean addMessageToSession(Long sessionId, MessageDTO messageDTO) {
        Optional<ChatSessionEntity> sessionOpt = chatRepository.findSessionById(sessionId);
        
        if (sessionOpt.isEmpty()) {
            log.error("Intento de añadir mensaje a sesión inexistente: {}", sessionId);
            return false;
        }
        
        ChatSessionEntity session = sessionOpt.get();
        
        // Crear la entidad de mensaje y vincularla a la sesión
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setChatSession(session);
        messageEntity.setContent(messageDTO.getContent());
        messageEntity.setRole(messageDTO.getRole());
        messageEntity.setTimestamp(LocalDateTime.now());
        messageEntity.setModelUsed(messageDTO.getModelUsed());
        
        // Guardar el mensaje
        chatRepository.saveMessage(messageEntity);
        
        // Actualizar la fecha de última actividad de la sesión
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
    @Transactional
    public boolean deleteSession(Long sessionId) {
        Optional<ChatSessionEntity> sessionOpt = chatRepository.findSessionById(sessionId);
        
        if (sessionOpt.isEmpty()) {
            log.error("Intento de eliminar sesión inexistente: {}", sessionId);
            return false;
        }
        
        // Primero eliminar todos los mensajes de la sesión
        chatRepository.deleteMessagesByChatSessionId(sessionId);
        
        // Luego eliminar la sesión
        chatRepository.deleteSessionById(sessionId);
        log.info("Sesión eliminada: {}", sessionId);
        return true;
    }

    /**
     * Actualiza la última actividad de una sesión.
     *
     * @param sessionId ID de la sesión
     */
    @Transactional
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