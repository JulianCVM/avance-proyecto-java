package com.ai.avance.business.IntegrationLogic;

import com.ai.avance.data.entities.AIEntities.AgentEntity;
import com.ai.avance.data.entities.ConversationEntities.MessageEntity;

import java.util.List;

/**
 * Interfaz para la integración con servicios de IA como OpenAI.
 */
public interface OpenAiIntegration {

    /**
     * Envía una solicitud de chat al servicio de IA y obtiene una respuesta.
     * 
     * @param messages Lista de mensajes de la conversación
     * @param agent Configuración del agente IA
     * @return Respuesta generada por el modelo de IA
     */
    String createChatCompletion(List<MessageEntity> messages, AgentEntity agent);
} 