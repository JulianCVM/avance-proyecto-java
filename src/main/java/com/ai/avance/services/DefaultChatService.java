package com.ai.avance.services;

import com.ai.avance.data.entities.ConversationEntities.DefaultChatSessionEntity;
import com.ai.avance.data.entities.ConversationEntities.DefaultMessageEntity;
import com.ai.avance.data.entities.UserEntities.UserEntity;
import com.ai.avance.data.repositories.DefaultChatSessionRepository;
import com.ai.avance.data.repositories.DefaultMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio para gestionar las operaciones de chat con el chatbot por defecto.
 */
@Service
@RequiredArgsConstructor
public class DefaultChatService {

    private final DefaultChatSessionRepository defaultChatSessionRepository;
    private final DefaultMessageRepository defaultMessageRepository;
    private final GeminiService geminiService;
    
    /**
     * Crea una nueva sesión de chat por defecto.
     */
    public DefaultChatSessionEntity createSession(UserEntity user) {
        DefaultChatSessionEntity session = new DefaultChatSessionEntity();
        session.setUser(user);
        session.setSessionKey(UUID.randomUUID().toString());
        session.setCreatedAt(LocalDateTime.now());
        session.setLastActivity(LocalDateTime.now());
        
        return defaultChatSessionRepository.save(session);
    }
    
    /**
     * Guarda una sesión de chat por defecto.
     */
    public DefaultChatSessionEntity saveSession(DefaultChatSessionEntity session) {
        return defaultChatSessionRepository.save(session);
    }
    
    /**
     * Busca una sesión de chat por defecto por su ID.
     */
    public Optional<DefaultChatSessionEntity> findSessionById(Long id) {
        return defaultChatSessionRepository.findById(id);
    }
    
    /**
     * Busca una sesión de chat por defecto por su clave de sesión.
     */
    public Optional<DefaultChatSessionEntity> findSessionBySessionKey(String sessionKey) {
        return defaultChatSessionRepository.findBySessionKey(sessionKey);
    }
    
    /**
     * Busca todas las sesiones de chat por defecto de un usuario.
     */
    public List<DefaultChatSessionEntity> findSessionsByUser(UserEntity user) {
        return defaultChatSessionRepository.findByUser(user);
    }
    
    /**
     * Busca una sesión por su ID y usuario.
     */
    public Optional<DefaultChatSessionEntity> findSessionByIdAndUser(Long id, UserEntity user) {
        return defaultChatSessionRepository.findByIdAndUser(id, user);
    }
    
    /**
     * Elimina una sesión de chat por defecto por su ID.
     */
    @Transactional
    public void deleteSessionById(Long id) {
        Optional<DefaultChatSessionEntity> sessionOpt = defaultChatSessionRepository.findById(id);
        
        sessionOpt.ifPresent(session -> {
            // Primero eliminar todos los mensajes
            defaultMessageRepository.deleteByChatSession(session);
            // Luego eliminar la sesión
            defaultChatSessionRepository.delete(session);
        });
    }
    
    /**
     * Guarda un nuevo mensaje en una sesión por defecto.
     */
    public DefaultMessageEntity saveMessage(DefaultMessageEntity message) {
        return defaultMessageRepository.save(message);
    }
    
    /**
     * Busca todos los mensajes de una sesión de chat por defecto.
     */
    public List<DefaultMessageEntity> findMessagesByChatSession(DefaultChatSessionEntity chatSession) {
        return defaultMessageRepository.findByChatSessionOrderByTimestampAsc(chatSession);
    }
    
    /**
     * Busca todos los mensajes de una sesión de chat por defecto por su ID.
     */
    public List<DefaultMessageEntity> findMessagesByChatSessionId(Long chatSessionId) {
        Optional<DefaultChatSessionEntity> sessionOpt = defaultChatSessionRepository.findById(chatSessionId);
        
        return sessionOpt
                .map(session -> defaultMessageRepository.findByChatSessionOrderByTimestampAsc(session))
                .orElse(List.of());
    }
    
    /**
     * Elimina todos los mensajes de una sesión de chat por defecto.
     */
    @Transactional
    public void deleteMessagesByChatSession(DefaultChatSessionEntity chatSession) {
        defaultMessageRepository.deleteByChatSession(chatSession);
    }
    
    /**
     * Procesa un mensaje del usuario y devuelve la respuesta del chatbot.
     * 
     * @param message El mensaje del usuario
     * @param sessionKey Clave de la sesión (si existe)
     * @return Respuesta del chatbot
     */
    @Transactional
    public String processMessage(String message, String sessionKey) {
        DefaultChatSessionEntity session;
        
        // Buscar la sesión existente o crear una nueva
        Optional<DefaultChatSessionEntity> existingSession = findSessionBySessionKey(sessionKey);
        if (existingSession.isPresent()) {
            session = existingSession.get();
        } else {
            // En un entorno real, obtendríamos el usuario de la sesión o autenticación
            // Por ahora, pasamos null para indicar un usuario anónimo
            session = createSession(null);
        }
        
        // Guardar el mensaje del usuario
        DefaultMessageEntity userMessage = new DefaultMessageEntity();
        userMessage.setChatSession(session);
        userMessage.setRole("user");
        userMessage.setContent(message);
        userMessage.setTimestamp(LocalDateTime.now());
        saveMessage(userMessage);
        
        // Actualizar timestamp de última actividad en la sesión
        session.setLastActivity(LocalDateTime.now());
        saveSession(session);
        
        try {
            // Obtener respuesta del modelo Gemini
            String botResponse = geminiService.generateResponse(message);
            
            // Guardar la respuesta del bot
            DefaultMessageEntity botMessage = new DefaultMessageEntity();
            botMessage.setChatSession(session);
            botMessage.setRole("assistant");
            botMessage.setContent(botResponse);
            botMessage.setTimestamp(LocalDateTime.now());
            saveMessage(botMessage);
            
            return botResponse;
        } catch (Exception e) {
            // En caso de error, registrar y devolver un mensaje predeterminado
            System.err.println("Error al procesar mensaje con Gemini: " + e.getMessage());
            e.printStackTrace();
            
            String fallbackResponse = "Lo siento, en este momento no puedo procesar tu solicitud. Por favor, intenta de nuevo más tarde.";
            
            // Guardar la respuesta de fallback
            DefaultMessageEntity botMessage = new DefaultMessageEntity();
            botMessage.setChatSession(session);
            botMessage.setRole("assistant");
            botMessage.setContent(fallbackResponse);
            botMessage.setTimestamp(LocalDateTime.now());
            saveMessage(botMessage);
            
            return fallbackResponse;
        }
    }
} 